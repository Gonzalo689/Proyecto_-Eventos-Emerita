package com.example.android_eventosemerita.api.model

import java.io.Serializable
import java.util.Date

class User(
    val id : Int, val nombre: String, val password: String, val email:String,
    val eventsLikeList: ArrayList<Int> = ArrayList()
): Serializable {
    constructor(id: Int, nombre: String, password: String, email: String) :
            this(id, nombre, password, email,ArrayList())

}