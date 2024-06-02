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
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
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
import com.example.android_eventosemerita.login.SignUp
import com.example.android_eventosemerita.notify.AlarmNotification
import com.example.android_eventosemerita.utils.UtilsConst.CHANNEL_ID
import com.example.android_eventosemerita.utils.UtilsConst.DP_KEYBOARD
import com.example.android_eventosemerita.utils.UtilsConst.USER_ID
import com.example.android_eventosemerita.utils.UtilsConst.userRoot
import com.example.android_eventosemerita.utils.UtilsFun.dpToPx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isBottomNavVisible = false
    private lateinit var eventAPIClient: EventAPIClient
    private lateinit var userAPIClient: UserAPIClient

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

        val calendar = Calendar.getInstance().apply {
            // Obtener la hora actual del sistema
            val horaActual = Calendar.getInstance()

            // Establecer la hora actual en el objeto Calendar
            set(Calendar.HOUR_OF_DAY, horaActual.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, horaActual.get(Calendar.MINUTE))
            set(Calendar.SECOND, horaActual.get(Calendar.SECOND))
        }
//        val calendar = Calendar.getInstance().apply {
//            set(year, month, day, /*hora*/ 10, /*minuto*/ 0, /*segundo*/ 0)
//        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
    fun cancelNotification(context: Context, eventId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(eventId)
        // Cancelar el alarm pendiente
        val intent = Intent(context, AlarmNotification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }


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

    fun loadFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }


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

    fun setBottomNavVisibility(visible: Boolean) {
        isBottomNavVisible = visible
    }



}