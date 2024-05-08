package com.example.android_eventosemerita.api.model

import java.io.Serializable
import java.util.Calendar

class Event(
    val titulo: String, val imagenIni: String, val descripcionBreve: String, val image: String,
    val fecha_inicio: String, val fecha_final: String, val urlEvent:String, val direccion:String,
    val descriptionCompleta: List<String>, val utlGooglemaps:String, val categoria:String,
    val destacado:Boolean, val eventId:Int
): Serializable {
    fun checkDate(dateNow:String):Int {
        val dateSplitStart = fecha_inicio.split(",")[0].split("-")

        val datePers = dateNow.split("-")

        val yearNow = datePers[0].toInt()
        val monthNow = datePers[1].toInt()
        val dayNow = datePers[2].toInt()

        val yearEvent = dateSplitStart[0].toInt()
        val monthEvent = dateSplitStart[1].toInt()
        val dayEvent = dateSplitStart[2].toInt()

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

        if (fecha_final.isEmpty()){
            return -1
        }
        val dateSplitFinal = fecha_final.split(",")[0].split("-")
        val yearEventFinal = dateSplitFinal[0].toInt()
        val monthEventFinal = dateSplitFinal[1].toInt()
        val dayEventFinal = dateSplitFinal[2].toInt()
        println("$yearEventFinal-$monthEventFinal-$dayEventFinal")
        if(yearEventFinal >= yearNow && monthEventFinal >= monthNow && dayEventFinal >= dayNow){
            return 0
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