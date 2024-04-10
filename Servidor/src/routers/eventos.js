const express = require('express')
const router = express.Router()
const {fetchEventsFromPages, getAllEventsFromMongoDB} = require("./scraper");

router.get('/', async (req, res) => {
    try {
        var eventos = await fetchEventsFromPages();
        res.json(eventos);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error interno del servidor");
    }
});

router.get('/all', async (req, res) => {
    try {
        var eventos = await getAllEventsFromMongoDB();
        res.json(eventos);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error interno del servidor");
    }
});


module.exports = router