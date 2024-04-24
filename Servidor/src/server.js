require("dotenv").config();
const express = require('express')
const bodyParser = require('body-parser')
const app = express()
const port = process.env.PORT ;
const eventosRouter = require('./routers/eventos');
const usuariosRouter = require('./routers/usuarios');

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());


app.use('/eventos', eventosRouter);

app.use('/usuarios', usuariosRouter);


app.get('/', (req, res) => {
    res.send('conect')
})

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})

  
