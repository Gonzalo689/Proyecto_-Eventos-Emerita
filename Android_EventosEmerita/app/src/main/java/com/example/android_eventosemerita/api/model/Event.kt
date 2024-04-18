package com.example.android_eventosemerita.api.model

class Event(
    val titulo: String, val imagenIni: String, val descripcionBreve: String, val image: String,
    val fecha_inicio: String, val fecha_final: String, val urlEvent:String, val direccion:String,
    val descriptionCompleta: List<String>, val utlGooglemaps:String, val categoria:String,
    val destacado:Boolean, val eventId:Int
) {
    override fun toString(): String {
        return "Event(titulo='$titulo', imagenIni='$imagenIni', descripcionBreve='$descripcionBreve', " +
                "image='$image', fecha_inicio='$fecha_inicio', fecha_final='$fecha_final', urlEvent='$urlEvent'," +
                " direccion='$direccion', descriptionCompleta=$descriptionCompleta, utlGooglemaps='$utlGooglemaps', " +
                "categoria='$categoria', destacado=$destacado, eventId=$eventId)"
    }
}