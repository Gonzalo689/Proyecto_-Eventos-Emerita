const { MongoClient} = require('mongodb');
const uri = 'mongodb://127.0.0.1:27017';
const dbName = 'Proyecto_Merida'; 

let client;

async function conectDB(nombreColeccion) {
    try {
        client = await MongoClient.connect(uri);
        console.log('Conexión establecida con la base de datos');

        const database = client.db(dbName);
        return database.collection(nombreColeccion);
        
    } catch (error) {
        console.error('Error al conectar con la base de datos:', error);
        throw error;
    }
}

async function closeDB() {
    try {
        if (client) {
            await client.close();
            console.log('Conexión cerrada correctamente');
        }
    } catch (error) {
        console.error('Error al cerrar la conexión con la base de datos:', error);
        throw error;
    }
}

module.exports = {
    conectDB,
    closeDB
};
