const express = require('express')
const router = express.Router()
const {scrapEventsFromPages} = require("./scraper");
const {getAllEventsDB} = require("./conectDB");


router.get('/', async (req, res) => {
    try {
        var eventos = await scrapEventsFromPages();
        res.json(eventos);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error interno del servidor");
    }
});

router.get('/all', async (req, res) => {
    try {
        var eventos = await getAllEventsDB();
        res.json(eventos);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error interno del servidor");
    }
});


module.exports = router