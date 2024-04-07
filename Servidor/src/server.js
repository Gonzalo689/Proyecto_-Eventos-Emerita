const express = require('express')
const app = express()
const port = 3000

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());




//'/api/getUsers' 
app.get('/', function(req, res)  {
    res.send('Hello World!')
})
app.get('/api/getUsers', function(req, res) {
    res.send('Lista de usuarios')
})
app.get('/api/getUser/:id', function(req, res) {
    console.log(req.params.id)
    res.send('Usuario ' + req.params.id)
})

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})