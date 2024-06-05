package com.example.android_eventosemerita.notify

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.activity.SplashScreen
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.utils.UtilsConst.CHANNEL_ID

/**
 * BroadcastReceiver para recibir y mostrar notificaciones de alarmas.
 */
class AlarmNotification:BroadcastReceiver() {
    /**
     * Método que se llama cuando se recibe una transmisión de difusión.
     *
     * @param context El contexto de la aplicación.
     * @param intent La intención recibida.
     */
    override fun onReceive(context: Context, intent: Intent?) {

        val events = intent?.getSerializableExtra("events") as? Event
        events?.let {
            createNotification(context, it)
        }

    }
    /**
     * Crea y muestra la notificación.
     *
     * @param context El contexto de la aplicación.
     * @param event El evento para el cual se crea la notificación.
     */
    private fun createNotification(context:Context, event:Event){
        val intent = Intent(context, SplashScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val flag = PendingIntent.FLAG_IMMUTABLE
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context , 0, intent,
            flag)

        val notification = NotificationCompat.Builder(context,CHANNEL_ID)
            .setSmallIcon(R.drawable.event_merida)
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(event.titulo)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(event.descripcionBreve)
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()


        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(event.eventId,notification)
    }
}