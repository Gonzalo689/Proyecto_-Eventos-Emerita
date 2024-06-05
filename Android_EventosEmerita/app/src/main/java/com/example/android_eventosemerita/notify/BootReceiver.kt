package com.example.android_eventosemerita.notify
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.utils.EventNotification
import java.io.Serializable
import android.app.PendingIntent
import android.app.AlarmManager
import java.util.*


/**
 * BroadcastReceiver que maneja el reinicio del dispositivo y la actualización de la aplicación.
 * Programa las notificaciones de eventos almacenados cuando se inicia el dispositivo.
 */
class BootReceiver : BroadcastReceiver() {
    /**
     * Método llamado cuando se recibe una transmisión de difusión.
     *
     * @param context El contexto de la aplicación.
     * @param intent La intención recibida.
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            // Recupera eventos almacenados
            val events = EventNotification.getAllEvents(context)
            for (event in events) {
                scheduleNotification(context, event)
            }
        }
    }

    /**
     * Programa una notificación para un evento en particular.
     *
     * @param context El contexto de la aplicación.
     * @param event El evento para el cual programar la notificación.
     */
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(context: Context, event: Event) {
        val dateSplit = event.fecha_inicio.split(",")[0].split("-")

        val year = dateSplit[0].trim().toInt()
        val month = dateSplit[1].trim().toInt()
        val day = dateSplit[2].trim().toInt()

        val bundle = Bundle().apply {
            putSerializable("events", event as Serializable)
        }
        val intent = Intent(context, AlarmNotification::class.java).apply {
            putExtras(bundle)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.eventId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            set(year, month, day, /*hora*/ 10, /*minuto*/ 0, /*segundo*/ 0)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
