const express = require('express')
const bodyParser = require('body-parser')
const cron = require('node-cron');
const app = express()
const port = process.env.PORT || 3000;
const eventosRouter = require('./routers/eventos');
const usuariosRouter = require('./routers/usuarios');

const { scrapEventsFromPages } = require("./scraper");
//nada
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());


app.use('/eventos', eventosRouter);

app.use('/usuarios', usuariosRouter);


app.get('/', (req, res) => {
    res.send('Hello World!')
})

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})

  
// Programar la tarea para que se ejecute cada hora (en el minuto 0 de cada hora)
cron.schedule('0 * * * *', scrapEventsFromPages);
process.on('SIGINT', () => {
    console.log('Deteniendo la ejecución de la tarea programada.');
    process.exit();
  });