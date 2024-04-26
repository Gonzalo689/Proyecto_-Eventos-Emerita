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
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.android_eventosemerita.notify.AlarmNotification
import com.example.android_eventosemerita.notify.AlarmNotification.Companion.NOTIFICATION_ID
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.EventAPIClient
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.ActivityMainBinding
import com.example.android_eventosemerita.fragments_nav.Home
import com.example.android_eventosemerita.fragments_nav.Search
import java.io.Serializable
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    companion object{
        const val CHANNEL_ID= "myChannel"
    }
    private lateinit var binding: ActivityMainBinding
    private var isBottomNavVisible = false
    private lateinit var eventAPIClient: EventAPIClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventAPIClient = EventAPIClient(applicationContext)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        hideNavKeyboard()

        loadFragment(Home(),false)

        setupBottomNavigationView()


        //funciona
//        createChannel()
//        newEvent()
    }

    private fun newEvent(){
        val callback = object : Callback.MyCallback<Event> {
            override fun onSuccess(data: Event) {
                println("Bien2")
                if (data != null) {
                    println("Bien")
                    sheduleNotification(data)
                }
            }
            override fun onError(errorMsg: String) {
                println("Error: $errorMsg")
            }
        }
        eventAPIClient.getNotify(callback)
    }


    @SuppressLint("ScheduleExactAlarm")
    private fun sheduleNotification(event: Event){
        val bundle = Bundle().apply {
            putSerializable("events", event as Serializable)
        }
        val intent = Intent(applicationContext, AlarmNotification::class.java).apply {
            putExtras(bundle)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmManager.setExact(
//            AlarmManager.RTC_WAKEUP,
//            Calendar.getInstance().timeInMillis+10000, pendingIntent )

        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.APRIL, 26, /*hora*/ 17, /*minuto*/ 52, /*segundo*/ 0)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Programa la alarma para la fecha específica
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )


    }
    private fun createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var channel = NotificationChannel(
                CHANNEL_ID,
                "mysuperChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Siii"
            }
            val notificationManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setupBottomNavigationView() {
        binding.nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(Home(),false)
                    true
                }
                R.id.search -> {
                    loadFragment(Search(),false)
                    true
                }

                else -> false
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


    private fun hideNavKeyboard(){
        binding.root.getViewTreeObserver().addOnGlobalLayoutListener(OnGlobalLayoutListener {
            val heightDiff: Int = binding.root.getRootView().getHeight() - binding.root.getHeight()
            if (heightDiff > dpToPx( 200) || isBottomNavVisible) {
                binding.nav.visibility = View.GONE
            } else {
                binding.nav.visibility = View.VISIBLE
            }
        })
    }
    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
    fun setBottomNavVisibility(visible: Boolean) {
        isBottomNavVisible = visible
    }

}