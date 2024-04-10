const express = require('express')
const router = express.Router()

//Datos para conectarse a MongoDB
const { MongoClient } = require('mongodb');
const uri = 'mongodb://127.0.0.1:27017';
const dbName = 'Proyecto_Merida'; 
const collectionName = 'Usuario';
const client = new MongoClient(uri);


router.post('/', async (req, res) => {
    try {
        await client.connect();
        const database = client.db(dbName);
        const collection = database.collection(collectionName);
        const user = req.body; 
        await collection.insertOne(user);
        console.error('Usuario creado:', user);
        res.status(200).send("usuario creado");
    } catch (error) {
        console.error('Error al crear usuario:', error);
        res.status(500).send("Error interno del servidor");
    }
});

router.get('/', async (req, res) => {
    try {
        await client.connect();
        const database = client.db(dbName);
        const collection = database.collection(collectionName);
        const usuarios = await collection.find({}).toArray();
        console.error('Usuarios encontrados:', usuarios);
        res.status(200).json(usuarios);;
    } catch (error) {
        console.error("Error al crear usuario:", error);
        res.status(500).send("Error al crear usuario");
    }
    
})


router.get('/', (req, res) => {
    res.send('Hello World!')
})

module.exports = router