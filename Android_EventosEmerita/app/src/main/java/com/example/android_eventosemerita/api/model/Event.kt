package com.example.android_eventosemerita.api.model

import java.io.Serializable
/**
 * Representa una categoría de eventos.
 */
class Event(
    val titulo: String, val imagenIni: String, val descripcionBreve: String, val image: String,
    val fecha_inicio: String, val fecha_final: String, val urlEvent:String, val direccion:String,
    val descriptionCompleta: List<String>, val utlGooglemaps:String, val categoria:String,
    val destacado:Boolean, val eventId:Int
): Serializable {
    /**
     * Método para verificar si la fecha del evento es anterior a la fecha actual.
     * @param dateNow Fecha actual en formato de cadena (AAAA-MM-DD).
     * @return 1 si la fecha del evento es posterior a la fecha actual, 0 si es igual y -1 si es anterior.
     */
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
        if(yearEventFinal >= yearNow && monthEventFinal >= monthNow && dayEventFinal >= dayNow){
            return 0
        }
        return -1
    }

    /**
     * Método para obtener una cadena que representa la fecha en formato legible.
     * @param date Fecha en formato de cadena (AAAA-MM-DD).
     * @return La fecha en formato legible (DD de MMMM del AAAA).
     */
    fun stringFecha(date:String):String{
        val dateSplit = date.split(",")[0].split("-")
        val dayNum = dateSplit[2][0]
        var day = dateSplit[2]
        if (dayNum == '0'){
            day = dateSplit[2][1].toString()
        }
        return day + " de " + nameMonth(dateSplit[1]) + " del " + dateSplit[0]
    }

    /**
     * Método para obtener el nombre del mes a partir de su número.
     * @param number Número del mes en formato de cadena (01 para enero, 02 para febrero, etc.).
     * @return El nombre del mes.
     */
    fun nameMonth(number: String) : String {
        val monthName = when (number) {
            "01" -> "enero"
            "02" -> "febrero"
            "03" -> "marzo"
            "04" -> "abril"
            "05" -> "mayo"
            "06" -> "junio"
            "07" -> "julio"
            "08" -> "agosto"
            "09" -> "septiembre"
            "10" -> "octubre"
            "11" -> "noviembre"
            "12" -> "diciembre"
            else -> error("Fallo al recoger el mes")
        }
        return monthName
    }
}