const axios = require('axios');
const cheerio = require('cheerio');
const { MongoClient } = require('mongodb');
//Datos para conectarse a MongoDB
const uri = 'mongodb://127.0.0.1:27017';
const dbName = 'Proyecto_Merida'; 
const collectionName = 'Eventos';
const client = new MongoClient(uri);

var newEvents = [];


function changeFormate(fecha) {
    if (fecha) {
        fecha = fecha.split('@');
        var fechaPartes = fecha[0].split(' ');
        var dia = fechaPartes[0];
        var mes = fechaPartes[1];
        var hora = fecha[1] ? fecha[1].trim() : '';

        var meses = { 'enero': 0,'febrero': 1, 'marzo': 2, 'abril': 3, 'mayo': 4, 'junio': 5, 'julio': 6,
            'agosto': 7, 'septiembre': 8, 'octubre': 9, 'noviembre': 10, 'diciembre': 11
        };
        
        var mesNumero = meses[mes.toLowerCase()];

        var fechaObjeto = new Date();
        fechaObjeto.setDate(parseInt(dia));
        fechaObjeto.setMonth(mesNumero); 
        fechaObjeto.setHours(hora ? parseInt(hora.split(':')[0]) : 0);
        fechaObjeto.setMinutes(hora ? parseInt(hora.split(':')[1]) : 0);
        fechaObjeto.setSeconds(0);
        if (hora.includes("pm")) {
            fechaObjeto.setHours(fechaObjeto.getHours() + 12);
        }

        return fechaObjeto.toLocaleString();

    }
}


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
                    let fecha_inicio = eventPage('h2 span.tribe-event-date-start').text().trim();
                    let fecha_final =  eventPage('h2 span.tribe-event-date-end').text().trim();
                    const direccion = eventPage('div.tribe-events-meta-group.tribe-events-meta-group-venue dl dd.tribe-venue').text().trim();
                    const utlGooglemaps= eventPage('a.tribe-events-gmap').attr('href') || '';
                    const categoria= eventPage('dd.tribe-events-event-categories').text().trim()|| '';

                    let allParagraphs = [];
                    eventPage('.tribe-events-single-event-description.tribe-events-content p').each((i, elem) => {
                        allParagraphs.push($(elem).text().trim());
                    });
                    fecha_inicio = changeFormate(fecha_inicio) || '';
                    fecha_final = changeFormate(fecha_final) || '';

                    return {titulo, imagenSrc, descripcionBreve, image, fecha_inicio, fecha_final, urlEvent, direccion, allParagraphs , utlGooglemaps, categoria};
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

    await client.connect();
    const database = client.db(dbName);
    const collection = database.collection(collectionName);
    const updateResult = await collection.updateOne(
        { titulo: event.titulo }, // Criterio de búsqueda: título del evento
        { $setOnInsert: event }, // Solo establece estos valores si se va a insertar
        { upsert: true } // Inserta un nuevo documento si no se encuentra ninguno con el título
    );

    if (updateResult.upsertedCount > 0) {
        newEvents.push(event);
        console.log("Evento insertado:", event.titulo);
    } else if (updateResult.matchedCount > 0) {
        console.log("Evento duplicado, no insertado:", event.titulo);
    }
   
}

// Función principal que gestiona el bucle de las páginas.
async function scrapEventsFromPages() {
    let allEvents = [];
    for (let i = 1; i <= 3; i++) {
        const pageEvents = await fetchAndProcessPage(i);
        allEvents = allEvents.concat(pageEvents);
    }

    const insertar = allEvents.map(event => saveEventToMongoDB(event));

    // Espera a que todas las promesas se resuelvan
    await Promise.all(insertar);

    console.log("Eventos insertados:", newEvents.length);
    return newEvents;
}


module.exports = {
    scrapEventsFromPages
  };
