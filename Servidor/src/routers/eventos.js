const express = require('express')
const cron = require('node-cron');
const router = express.Router()
const {scrapEventsFromPages} = require("../scraper");
const {conectDB, closeDB } = require("../dataBase");

var eventos=[];

//Conexion
const collectionName = "eventos";

router.get('/scrap', async (req, res) => {
    try {
        console.log("Nuevos eventos", eventos.length);
        res.json(eventos);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error interno del servidor");
    }
});

// Programar la tarea para que se ejecute cada hora (en el minuto 0 de cada hora) 
cron.schedule('0 * * * *', async () => {
    try {
        eventos=[]
        eventos = await scrapEventsFromPages(); 
    } catch (error) {
        console.error("Error al obtener eventos:", error);
    }
});

process.on('SIGINT', () => {
    console.log('Deteniendo la ejecución de la tarea programada.');
    process.exit();
});


router.get('/', async (req, res) => {
    try {
        console.log("Buscando todo eventos en la base de datos...");
        var fechaActual = Date.now();
        const collection = await conectDB(collectionName);
        const events = await collection.find({}).toArray();
        console.log("Eventos encontrados: ",events.length)
        res.json(events);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error interno del servidor");
    } finally {
        await closeDB();
    }
});

router.get('/past', async (req, res) => {
    try {
        console.log("Eventos pasados");
        var fechaActual = Date.now();
        const collection = await conectDB(collectionName);
        const events = await collection.find({}).toArray();

        const eventosPasados = events.filter(evento => {
            const fechaComparar = evento.fecha_final ? new Date(evento.fecha_final) : new Date(evento.fecha_inicio);
            return fechaComparar < fechaActual;
        });

        console.log(eventosPasados.length, "eventos encontrados");
        res.json(eventosPasados);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error interno del servidor");
    } finally {
        await closeDB();
    }
});


router.get('/destacados', async (req, res) => {
    try {
        // Obteniendo la fecha actual
        var fechaActual = Date.now();

        console.log("Buscando eventos destacados en la base de datos...");
        const collection = await conectDB(collectionName);

        const events = await collection.find({ "destacado": true }).toArray();

        const eventosFuturos = events.filter(evento => {
            const fechaComparar = evento.fecha_final ? new Date(evento.fecha_final) : new Date(evento.fecha_inicio);
            return fechaComparar >= fechaActual;
        });

        console.log(eventosFuturos.length, "eventos encontrados");
        res.json(eventosFuturos);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error interno del servidor");
    } finally {
        await closeDB();
    }
});
router.get('/:categoria', async (req, res) => {
    try {
        // Obteniendo la fecha actual
        var fechaActual = Date.now();

        console.log("Buscando eventos destacados en la base de datos...");
        const collection = await conectDB(collectionName);

        const regex = new RegExp(`\\b${req.params.categoria}\\b`, 'i');

        const events = await collection.find({ 
            "categoria": { $regex: regex } 
        }).toArray();

        const eventosFuturos = events.filter(evento => new Date(evento.fecha_inicio) >= fechaActual);
       
        console.log(eventosFuturos.length, "eventos encontrados");

        res.json(eventosFuturos);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error interno del servidor");
    } finally {
        await closeDB();
    }
});


router.get('/id/:id', async (req, res) => {
    try {
        const eventId = parseInt(req.params.id);
        console.log("Buscando un evento con id:", eventId);
        const collection = await conectDB(collectionName);
        const evento = await collection.findOne({ "eventId" : eventId });
        
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