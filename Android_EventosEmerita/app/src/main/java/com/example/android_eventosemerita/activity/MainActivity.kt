package com.example.android_eventosemerita.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.EventAPIClient
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.databinding.ActivityMainBinding
import com.example.android_eventosemerita.fragments_nav.Favorite
import com.example.android_eventosemerita.fragments_nav.Home
import com.example.android_eventosemerita.fragments_nav.Profile
import com.example.android_eventosemerita.fragments_nav.Search
import com.example.android_eventosemerita.notify.AlarmNotification
import com.example.android_eventosemerita.utils.EventNotification.removeEvent
import com.example.android_eventosemerita.utils.EventNotification.saveEvent
import com.example.android_eventosemerita.utils.UtilsConst.CHANNEL_ID
import com.example.android_eventosemerita.utils.UtilsConst.USER_ID
import com.example.android_eventosemerita.utils.UtilsConst.userRoot
import com.example.android_eventosemerita.utils.UtilsFun.dpToPx
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Actividad principal que muestra la interfaz de usuario y gestiona la navegación entre fragmentos.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isBottomNavVisible = false
    private lateinit var eventAPIClient: EventAPIClient
    private lateinit var userAPIClient: UserAPIClient

    /**
     * Método que se ejecuta cuando se crea la actividad.
     * @param savedInstanceState Estado guardado de la actividad, si lo hay.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventAPIClient = EventAPIClient(applicationContext)
        userAPIClient = UserAPIClient(applicationContext)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bubbleTabBar.visibility = View.GONE

        lifecycleScope.launch {
            getUser()
            loadFragment(Home(),false)
        }
        hideIfKeyboardOut()
        navBbuble()
        //funciona crear canal
        createChannel()


    }
    /**
     * Método para obtener el usuario actual, si el id es 0 significara que se registraron como invitado.
     */
    private suspend fun getUser() {
        return suspendCoroutine { continuation ->
            val callback = object : Callback.MyCallback<User> {
                override fun onSuccess(data: User) {
                    userRoot = data
                    binding.bubbleTabBar.visibility = View.VISIBLE
                    continuation.resume(Unit)
                }

                override fun onError(errorMsg: User?) {
                    continuation.resume(Unit)
                }
            }
            val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
            val id = preferences.getInt(USER_ID, 0)

            if (id == 0) {
                userRoot = null
                continuation.resume(Unit)
            } else {
                userAPIClient.getUserById(id, callback)
            }

        }
    }
    /**
     * Método para programar una notificación para un evento.
     * @param event El evento para el que se programará la notificación.
     */

    @SuppressLint("ScheduleExactAlarm")
    fun sheduleNotification(event: Event){
        val dateSplit = event.fecha_inicio.split(",")[0].split("-")

        val year = dateSplit[0].trim().toInt()
        val month = dateSplit[1].trim().toInt()
        val day = dateSplit[2].trim().toInt()

        println("$year --- $month ---- $day")

        val bundle = Bundle().apply {
            putSerializable("events", event as Serializable)
        }
        val intent = Intent(applicationContext, AlarmNotification::class.java).apply {
            putExtras(bundle)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            event.eventId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

//        val calendar = Calendar.getInstance().apply {
//            val horaActual = Calendar.getInstance()
//
//            set(Calendar.HOUR_OF_DAY, horaActual.get(Calendar.HOUR_OF_DAY))
//            set(Calendar.MINUTE, horaActual.get(Calendar.MINUTE))
//            set(Calendar.SECOND, horaActual.get(Calendar.SECOND))
//        }
        val calendar = Calendar.getInstance().apply {
            set(year, month, day, /*hora*/ 10, /*minuto*/ 0, /*segundo*/ 0)
        }


        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        saveEvent(applicationContext, event)
    }

    /**
     * Método para cancelar una notificación.
     * @param context El contexto de la aplicación.
     * @param eventId El ID del evento cuya notificación se cancelará.
     */
    fun cancelNotification(context: Context, eventId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(eventId)

        val intent = Intent(context, AlarmNotification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.let {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(it)
            it.cancel()
        }
        removeEvent(context, eventId)
    }

    /**
     * Método para crear un canal de notificación para versiones de Android Oreo (API nivel 26) o superior.
     */
    private fun createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Chanel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Chanel for notifications"
            }
            val notificationManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    /**
     * Método para gestionar la navegación con navigationBar con un estilo de burbujas.
     */
    private fun navBbuble(){
        binding.bubbleTabBar.addBubbleListener { id ->
            when (id) {
                R.id.home -> {
                    loadFragment(Home(), false)
                }
                R.id.search -> {
                    loadFragment(Search(), false)
                }
                R.id.favorite -> {
                    loadFragment(Favorite(), false)
                }
                R.id.profile -> {
                    loadFragment(Profile(), false)
                }
            }
        }
    }
    /**
     * Método para cargar un fragmento en el contenedor.
     * @param fragment El fragmento que se cargará.
     * @param addToBackStack Booleano que indica si se agrega a la pila de retroceso.
     */
    fun loadFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
    /**
     * Método para ocultar la barra de navegación si el teclado está visible.
     */
    fun hideIfKeyboardOut() {
        val heightDiffThreshold = dpToPx(applicationContext)
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff: Int = binding.root.rootView.height - binding.root.height

            if (heightDiff > heightDiffThreshold || isBottomNavVisible) {
                binding.bubbleTabBar.visibility = View.GONE
            } else {
                binding.bubbleTabBar.visibility = View.VISIBLE
            }

        }
    }

    /**
     * Comprobar si esta visile el componente de navegación
     */
    fun setBottomNavVisibility(visible: Boolean) {
        isBottomNavVisible = visible
    }



}