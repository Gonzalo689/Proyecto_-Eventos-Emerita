//Datos para conectarse a MongoDB
const { MongoClient } = require('mongodb');
const uri = 'mongodb://127.0.0.1:27017';
const dbName = 'Proyecto_Merida'; 
const collectionName = 'Eventos';
const client = new MongoClient(uri);

// Eventos
async function getAllEventsDB() {
    await client.connect();
    const database = client.db(dbName);
    const collection = database.collection(collectionName);
    const events = await collection.find({}).toArray();
    return events;
}

module.exports = {
    getAllEventsDB
  };


//Usuarios