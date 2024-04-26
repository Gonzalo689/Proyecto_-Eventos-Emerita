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
import android.os.Handler
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.android_eventosemerita.AlarmNotification
import com.example.android_eventosemerita.AlarmNotification.Companion.NOTIFICATION_ID
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.databinding.ActivityMainBinding
import com.example.android_eventosemerita.fragments_nav.Home
import com.example.android_eventosemerita.fragments_nav.Search
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    companion object{
        const val CHANNEL_ID= "myChannel"
    }


    private lateinit var binding: ActivityMainBinding
    private var isBottomNavVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        hideNavKeyboard()

        loadFragment(Home(),false)

        setupBottomNavigationView()


        //fun
        createChannel()
        sheduleNotification()


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

    @SuppressLint("ServiceCast", "ScheduleExactAlarm")
    private fun sheduleNotification(){
        val intent = Intent(applicationContext,AlarmNotification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,Calendar.getInstance().timeInMillis + 15000, pendingIntent )

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