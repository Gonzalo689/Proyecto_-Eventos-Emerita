const express = require('express')
const router = express.Router()

const {conectDB, closeDB } = require("../dataBase");
//Conexion
const collectionName = "comentarios";

function getCurrentDate() {
    return new Date().toISOString().split('T')[0];
}

router.get('/', async (req, res) => {
    try {
        console.log("Buscando todos los comentarios");
        const collection = await conectDB(collectionName);
        const coments = await collection.find({}).toArray();
        console.log("Eventos encontrados: ",coments.length)
        res.json(coments);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error interno del servidor");
    } finally {
        await closeDB();
    }
});

router.get('/:id', async (req, res) => {
    try {
        
        const eventId = parseInt(req.params.id);
        console.log("Buscandolos comentarios: ", eventId);
        const collection = await conectDB(collectionName);
        const comentarios = await collection.find({ idPost: eventId }).toArray();
        
        if (comentarios.length > 0) {
            console.log('Comentarios encontrados:', comentarios.length);
            res.status(200).json(comentarios);
        } else {
            console.error('No se encontraron comentarios');
            res.status(404).send('No se encontraron comentarios');
        }
    } catch (error) {
        console.error("Error al encontrar comentarios:", error);
        res.status(500).send("Error al encontrar comentarios");
    }finally {
        await closeDB();
    }
})

async function getMaxUserId(collection) {
    const result = await collection.findOne({}, { projection: { id: 1, _id: 0 }, sort: { id: -1 } });
    console.log("Resultado de getMaxEventId:", result);

    return result ? result.id : 0;
}
// Crear un usuario nuevo
router.post('/', async (req, res) => {
    try {
        console.log("Crear nuevo comentario");
        const collection = await conectDB(collectionName);
        
        const coment = req.body;
        var id = await getMaxUserId(collection)
        id++;
        
        coment.fecha = getCurrentDate();
        coment.id = id;
        await collection.insertOne(coment);

        console.log('comentario creado:', coment);
        res.status(200).json(coment); 
    } catch (error) {
        console.error("Error al crear usuario:", error);
        res.status(500).send("Error al crear usuario");
    } finally {
        await closeDB();
    }
})


router.post('/:idComentario/coment', async (req, res) => {
    try {
        console.log("Agregar nuevo comentario a un comentario existente");
        const collection = await conectDB(collectionName);
        
        const idComentario = parseInt(req.params.idComentario);
        const nuevoComentario = req.body;
        nuevoComentario.fecha = getCurrentDate();

        // Encontrar el comentario al que se va a agregar el nuevo comentario
        const comentarioExistente = await collection.findOne({ id: idComentario });
        if (!comentarioExistente) {
            return res.status(404).json({ error: "Comentario no encontrado" });
        }
        
        var id = comentarioExistente.listComents.length
        console.log("id: ", id);
        
        comentarioExistente.listComents = comentarioExistente.listComents || [];
        nuevoComentario.id = id;
        
        comentarioExistente.listComents.push(nuevoComentario);
        await collection.updateOne({ id: idComentario }, { $set: { listComents: comentarioExistente.listComents } });

        console.log('Nuevo comentario agregado:', nuevoComentario);
        res.status(200).json(nuevoComentario);
    } catch (error) {
        console.error("Error al agregar nuevo comentario:", error);
        res.status(500).send("Error al agregar nuevo comentario");
    } finally {
        await closeDB();
    }
});

module.exports = router