const express = require('express')
const app = express()
const port = process.env.PORT || 3000;
const scrapeEvents = require('./scraper');

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());


app.use('/', scrapeEvents);


app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})