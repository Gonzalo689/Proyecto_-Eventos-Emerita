package com.example.android_eventosemerita.utils

import android.content.Context
import com.example.android_eventosemerita.api.model.Event
import com.google.gson.Gson

/**
 * Clase de utilidad para la gestión de eventos notificados.
 */
object EventNotification {
    /**
     * Guarda un evento en las preferencias compartidas.
     *
     * @param context El contexto de la aplicación.
     * @param event El evento a guardar.
     */
    fun saveEvent(context: Context, event: Event) {
        val sharedPreferences = context.getSharedPreferences("events_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("event_${event.eventId}", Gson().toJson(event))
        editor.apply()
    }

    /**
     * Elimina un evento de las preferencias compartidas.
     *
     * @param context El contexto de la aplicación.
     * @param eventId El ID del evento a eliminar.
     */
    fun removeEvent(context: Context, eventId: Int) {
        val sharedPreferences = context.getSharedPreferences("events_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("event_$eventId")
        editor.apply()
    }

    /**
     * Obtiene todos los eventos almacenados en las preferencias compartidas.
     *
     * @param context El contexto de la aplicación.
     * @return Una lista de eventos.
     */
    fun getAllEvents(context: Context): List<Event> {
        val sharedPreferences = context.getSharedPreferences("events_prefs", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        val events = mutableListOf<Event>()
        for (entry in allEntries) {
            val eventJson = entry.value as String
            val event = Gson().fromJson(eventJson, Event::class.java)
            events.add(event)
        }
        return events
    }
}