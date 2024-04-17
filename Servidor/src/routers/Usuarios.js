const express = require('express')
const router = express.Router()

const {conectDB, closeDB } = require("./dataBase");
//Conexion
const collectionName = "usuarios";

router.post('/', async (req, res) => {
    try {
        const collection = await conectDB(collectionName);
        const user = req.body; 
        await collection.insertOne(user);
        console.error('Usuario creado:', user);
        res.status(200).send("usuario creado");
    } catch (error) {
        console.error('Error al crear usuario:', error);
        res.status(500).send("Error interno del servidor");
    }finally {
        await closeDB();
    }
});

router.get('/', async (req, res) => {
    try {
        const collection = await conectDB(collectionName);
        const usuarios = await collection.find({}).toArray();
        console.error('Usuarios encontrados:', usuarios.length);
        res.status(200).json(usuarios);;
    } catch (error) {
        console.error("Error al crear usuario:", error);
        res.status(500).send("Error al crear usuario");
    }finally {
        await closeDB();
    }
    
})
router.get('/:id', async (req, res) => {
    try {
        const collection = await conectDB(collectionName);
        const userId = parseInt(req.params.id);
        console.error(userId);
        const usuario = await collection.findOne({ id : userId });
        
        if (usuario) {
            console.error('Usuario encontrado:', usuario);
            res.status(200).json(usuario);
        } else {
            console.error('Usuario no encontrado');
            res.status(404).send('Usuario no encontrado');
        }
    } catch (error) {
        console.error("Error al encontrar usuario:", error);
        res.status(500).send("Error al encontrar usuario");
    }finally {
        await closeDB();
    }
})


router.get('/', (req, res) => {
    res.send('Hello World!')
})

module.exports = router