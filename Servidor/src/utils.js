// src/utils.js
const fs = require("fs");


// // Modificamos esta función para retornar un nombre fijo
// const generateFilename = () => {
//   // Nombre fijo para el archivo
//   const filename = "events-data.json";
//   return filename;
// };

// const saveEventJson = (event) => {
//   // Crea un nuevo archivo o sobrescribe el existente usando el nombre fijo
//   const filename = generateFilename();
//   const jsonEvents = JSON.stringify(event, null, 2);

//   const folder = process.env.NODE_ENV === "test" ? "test-data" : "data";
//   if (!fs.existsSync(folder)) {
//     fs.mkdirSync(folder);
//   }
//   fs.writeFileSync(`${folder}/${filename}`, jsonEvents);

//   // Retorna la ruta del archivo
//   return `${folder}/${filename}`;
// };


module.exports = {
  saveEventToMongoDB
};
