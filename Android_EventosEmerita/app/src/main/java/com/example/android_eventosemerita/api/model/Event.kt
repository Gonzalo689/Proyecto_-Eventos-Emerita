package com.example.android_eventosemerita.api.model

import java.io.Serializable
import java.util.Calendar

class Event(
    val titulo: String, val imagenIni: String, val descripcionBreve: String, val image: String,
    val fecha_inicio: String, val fecha_final: String, val urlEvent:String, val direccion:String,
    val descriptionCompleta: List<String>, val utlGooglemaps:String, val categoria:String,
    val destacado:Boolean, val eventId:Int
): Serializable {
    fun checkDate(date: String):Int {
        val dateSplit = date.split(",")[0].split("-")

        val calendar = Calendar.getInstance()
        val yearNow = calendar.get(Calendar.YEAR)
        val monthNow = calendar.get(Calendar.MONTH) + 1
        val dayNow = calendar.get(Calendar.DAY_OF_MONTH)
        val yearEvent = dateSplit[0].toInt()
        val monthEvent = dateSplit[1].toInt()
        val dayEvent = dateSplit[2].toInt()

        if (yearNow == yearEvent) {
            if (monthNow == monthEvent) {
                if (dayNow < dayEvent) {
                    return 1
                }
                if (dayNow == dayEvent){
                    return 0
                }
            }
            if (monthNow < monthEvent){
                return 1
            }
        }
        if (yearNow < yearEvent){
            return 1
        }
        return -1
    }
    fun stringFecha(date:String):String{
        val dateSplit = date.split(",")[0].split("-")
        return dateSplit[2] + " de " + nameMonth(dateSplit[1]) + " del " + dateSplit[0]
    }
    fun nameMonth(number: String) : String {
        val monthName = when (number) {
            "01" -> "Enero"
            "02" -> "Febrero"
            "03" -> "Marzo"
            "04" -> "Abril"
            "05" -> "Mayo"
            "06" -> "Junio"
            "07" -> "Julio"
            "08" -> "Agosto"
            "09" -> "Septiembre"
            "10" -> "Octubre"
            "11" -> "Noviembre"
            "12" -> "Diciembre"
            else -> error("Fallo al recoger el mes")
        }
        return monthName
    }
}