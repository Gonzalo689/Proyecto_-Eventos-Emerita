package com.example.android_eventosemerita.api.model

import java.io.Serializable

/**
 * Representa un usuario.
 */
class User(
    val id : Int, var nombre: String, val password: String, var email:String,
    val eventsLikeList: ArrayList<Int> = ArrayList(), var profilePicture: String
): Serializable {
    /**
     * Constructor alternativo de la clase User sin la lista de eventos favoritos.
     * @param id ID del usuario.
     * @param nombre Nombre del usuario.
     * @param password Contraseña del usuario.
     * @param email Correo electrónico del usuario.
     * @param profilePicture URL de la imagen de perfil del usuario.
     */
    constructor(id: Int, nombre: String, password: String, email: String, profilePicture: String) :
            this(id, nombre, password, email,ArrayList(),profilePicture)

}