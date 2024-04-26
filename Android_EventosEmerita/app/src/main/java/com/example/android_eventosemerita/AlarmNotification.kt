package com.example.android_eventosemerita

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.activity.MainActivity.Companion.CHANNEL_ID
import com.example.android_eventosemerita.activity.SplashScreen

class AlarmNotification:BroadcastReceiver() {
    companion object{
        const val NOTIFICATION_ID = 1
    }
    override fun onReceive(context: Context, intent: Intent?) {
        crearSimpleNotificacion(context)

    }

    fun crearSimpleNotificacion(context:Context){

        val intent2 = Intent(context, SplashScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context , 0, intent2,
            flag)

        val notification = NotificationCompat.Builder(context,CHANNEL_ID)
            .setSmallIcon(R.drawable.prueba)
            .setContentTitle("Titulo")
            .setContentText("Contenido prueba")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText("jeje nice")
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()


        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID,notification)
    }
}