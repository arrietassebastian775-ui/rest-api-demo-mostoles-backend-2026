/**
 * JSON => JavaScript Object Notation. 
 * 
 * El JSON es un formato de intercambio de informacion, que antiguamente lo que se utilizaba era XML
 * para el intercambio de informacion entre aplicaciones desarrolladas en lenguajes de programacion 
 * diferentes.
 * 
 * El formato XML tenia la desventajas, y las sigue teniendo, de necesitar de herramientas externas para
 * para parsear este formato, y tampoco permitia arrays dentro de un XML
 * 
 * El formato de JSON no necesita de herramientas externas para el parseo (analisis), y SI que permite representar
 * arrays.
 * 
 */

/**
 * Un Objeto Literal de JavaScript puede ser creado de la siguiente forma:
 * 
 * Que tambien podria ser creado a partir de clases, igual que en Java
 */

const persona = {
    nombre: "Victor Rafael",
    apellidos: "Machado Arteaga",
    edad: 62,
    libros: [
        {autor: "Pepito", titulo: "Habia una vez"},
        {autor: "Margatita", titulo: "La vida es bella"}
    ]
};

/**
 * Para pasar del objeto anterior al formato de JSON solamente tendriamos que encerrar las propiedades entre
 * comillas dobles, para lo cual existen ya metodos en JavaScript para hacerlo
 */

// Un JSON va a ser una cadena (string con un objeto de JavaScript, con las propiedades encerradas entre comillas
// dobles)
const personaJSON = JSON.stringify(persona);

console.log(personaJSON);

// Para obtener un objeto de JavaScript a partir del string personaJSON

const persona2 = JSON.parse(personaJSON);

console.log(persona2.nombre);
console.log(persona2.apellidos);