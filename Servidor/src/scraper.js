const axios = require('axios');
const cheerio = require('cheerio');
//const {saveEventToMongoDB } = require("./utils");
const { MongoClient } = require('mongodb');

// Esta función se encarga de procesar cada página individual.
async function fetchAndProcessPage(pageNumber) {
    const website = `https://merida.es/agenda/lista/p%C3%A1gina/${pageNumber}/`;

    try {
        const response = await axios.get(website);
        if (response.status === 200) {
            const $ = cheerio.load(response.data);
            const events = [];

            const eventPromises = $('div.tribe-events-calendar-list__event-wrapper.tribe-common-g-col').map(async (index, element) => {
                let article = $(element).find('article');

                const imagenSrc = article.find('div > a > img').attr('src');
                const descripcionBreve = article.find('div.tribe-events-calendar-list__event-description.tribe-common-b2.tribe-common-a11y-hidden p').text().trim();
                const urlEvent = article.find('a.tribe-events-calendar-list__event-title-link.tribe-common-anchor-thin').attr('href');

                const eventResponse = await axios.get(urlEvent);
                if (eventResponse.status === 200) {
                    const eventPage = cheerio.load(eventResponse.data);

                    const titulo = eventPage('h1.tribe-events-single-event-title').text().trim();
                    let image = eventPage('.tribe-events-single-event-description.tribe-events-content p a img').attr('src') || eventPage('.tribe-events-single-event-description.tribe-events-content div a img').first().attr('src');
                    const fecha_inicio = eventPage('h2 span.tribe-event-date-start').text().trim();
                    const fecha_final = eventPage('h2 span.tribe-event-date-end').text().trim();
                    const direccion = eventPage('div.tribe-events-meta-group.tribe-events-meta-group-venue dl dd.tribe-venue').text().trim();

                    let allParagraphsText = "";
                    eventPage('.tribe-events-single-event-description.tribe-events-content p').each((i, elem) => {
                      allParagraphsText += $(elem).text().trim();
                    });

                    return { titulo, imagenSrc, descripcionBreve, image, fecha_inicio, fecha_final, urlEvent, direccion, allParagraphsText};
                }
            }).get(); // Convertir a un array real para que Promise.all pueda manejarlo

            const results = await Promise.all(eventPromises);
            results.forEach(event => {
                if (event) events.push(event);
            });

            return events;
        } else {
            console.error("Error al cargar la página:", response.status);
            return [];
        }
    } catch (error) {
        console.error("Error al realizar la solicitud:", error);
        return [];
    }
}

async function saveEventToMongoDB(event) {
    const uri = 'mongodb://127.0.0.1:27017'; // Cambia esta URI por la URI de tu base de datos MongoDB
    const dbName = 'Proyecto_Merida'; // Cambia esto por el nombre de tu base de datos
  
    const client = new MongoClient(uri);
  
    try {
        await client.connect();
        const database = client.db(dbName);
        const collection = database.collection('Eventos');
        // Inserta el evento en la colección de eventos
        //await collection.insertOne(event);
        const updateResult = await collection.updateOne(
            { titulo: event.titulo }, // Criterio de búsqueda: título del evento
            { $setOnInsert: event }, // Solo establece estos valores si se va a insertar
            { upsert: true } // Inserta un nuevo documento si no se encuentra ninguno con el título
        );

        // Log de resultado
        if (updateResult.upsertedCount > 0) {
            console.log("Evento insertado:", event.titulo);
        } else if (updateResult.matchedCount > 0) {
            console.log("Evento duplicado, no insertado:", event.titulo);
        }
    } finally {
        await client.close();
    }
  }

// Función principal que gestiona el bucle de las páginas.
async function fetchEventsFromPages() {
    let allEvents = [];
    for (let i = 1; i <= 3; i++) {
        const pageEvents = await fetchAndProcessPage(i);
        allEvents = allEvents.concat(pageEvents);
    }

    allEvents.forEach(async event => {
        await saveEventToMongoDB(event);
    });
    //saveEventJson(allEvents); // Guardar los eventos en un archivo JSON
}

// Llamamos a la función principal para iniciar el proceso.
fetchEventsFromPages();
