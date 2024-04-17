const express = require('express')
const router = express.Router()
const {scrapEventsFromPages} = require("./scraper");
const {conectDB, closeDB } = require("./dataBase");
//Conexion
const collectionName = "eventos";

setInterval(async () => {
    try {
        const datosObtenidos = await scrapEventsFromPages();
        console.log("Datos obtenidos:", datosObtenidos);
        // Aquí puedes realizar operaciones adicionales con los datosObtenidos
    } catch (error) {
        console.error("Error al ejecutar scrapEventsFromPages:", error);
    } finally {
        await closeDB();
    }
}, 3600000);


router.get('/scrap', async (req, res) => {
    try {
        console.error('Buscando eventos...');
        var eventos = await scrapEventsFromPages();
        res.json(eventos);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error interno del servidor");
    }
});

router.get('/', async (req, res) => {
    try {
        const collection = await conectDB(collectionName);
        const events = await collection.find({}).toArray();
        res.json(events);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error interno del servidor");
    } finally {
        await closeDB();
    }
});
router.get('/:id', async (req, res) => {
    try {
        const collection = await conectDB(collectionName);
        const eventId = parseInt(req.params.id);
        const evento = await collection.findOne({ eventId : eventId });
        
        if (evento) {
            console.error('Evento encontrado:', evento);
            res.status(200).json(evento);
        } else {
            console.error('Evento no encontrado');
            res.status(404).send('Evento no encontrado');
        }
    } catch (error) {
        console.error("Error al Encontrar evento:", error);
        res.status(500).send("Error al encontrar evento");
    } finally {
        await closeDB();
    }
})


module.exports = router