package com.example.android_eventosemerita.api.model

/**
 * Representa una categoría de comentarios.
 */
class Coment (val id:Int, val texto: String,val fecha:String, var listComents: List<Coment>, var idUser:Int, var idPost:Int){

}