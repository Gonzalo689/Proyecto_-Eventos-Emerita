const express = require('express')
const bodyParser = require('body-parser')
const app = express()
const port = process.env.PORT || 3000;
const eventsRouter = require('./routers/Events');
//nada
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());


app.use('/events', eventsRouter);

app.get('/', (req, res) => {
    res.send('Hello World!')
})

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})