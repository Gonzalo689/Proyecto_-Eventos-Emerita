const request = require('request');
const cheerio = require('cheerio');
const axios = require('axios');
//const router = require("express").Router();


// router.post("/scrape", async (req, res) => {


// });
const website = "https://merida.es/agenda/lista/p%C3%A1gina/1/";

// Realizamos la petición a la página web
axios.get(website)
  .then((response) => {
    if (response.status === 200) {
      const $ = cheerio.load(response.data);

      console.log("----");

      $('div.tribe-events-calendar-list__event-wrapper.tribe-common-g-col').each((index, element) => {
        let article = $(element).find('article');

        const imagenDiv = article.find('div > a > img');
        const imagenSrc = imagenDiv.attr('src');
        
        const descripcionDiv = article.find('div.tribe-events-calendar-list__event-description.tribe-common-b2.tribe-common-a11y-hidden');
        const descripcionBreve = descripcionDiv.find('p').text().trim();

        const urlEvent = article.find('a.tribe-events-calendar-list__event-title-link.tribe-common-anchor-thin').attr('href');

        axios.get(urlEvent)
          .then((response) => {
            if (response.status === 200) {
              const $ = cheerio.load(response.data);
              console.log("-----");
              const titulo = $('h1.tribe-events-single-event-title').text().trim();
              console.log("Imagen:", imagenSrc);
              console.log("Titulo:", titulo);
              console.log("Descripción Breve:", descripcionBreve);

              let allParagraphsText = "";
              $('.tribe-events-single-event-description.tribe-events-content p').each((i, elem) => {
                allParagraphsText += $(elem).text().trim() + "\n";
              });
          
              console.log(allParagraphsText);

              let image = $('.tribe-events-single-event-description.tribe-events-content p a img').attr('src');
              if (!image) {
                image = $('.tribe-events-single-event-description.tribe-events-content div a img').first().attr('src');
              } 
              console.log("imagen:", image);

              const fecha_inicio = $('h2 span.tribe-event-date-start').text().trim();
              const fecha_final = $('h2 span.tribe-event-date-end').text().trim();
              
              console.log("Fecha de inicio:", fecha_inicio);
              console.log("Fecha de finalización:", fecha_final);

              const direccion = $('div.tribe-events-meta-group.tribe-events-meta-group-venue dl dd.tribe-venue').text().trim();
              console.log("Dirección:", direccion);
            } else {
              console.error("Error al cargar la página del evento:", response.status);
            }
          })
          .catch((error) => {
            console.error("Error al cargar la página del evento:", error);
          });

      });

    } else {
      console.error("Error al cargar la página:", response.status);
    }
  })
  .catch((error) => {
    console.error("Error al realizar la solicitud:", error);
  });


//module.exports = router;