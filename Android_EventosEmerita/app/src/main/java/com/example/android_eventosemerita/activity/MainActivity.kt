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
import com.example.android_eventosemerita.login.SignIn
import com.example.android_eventosemerita.login.SignIn.Companion.USER_ID
import java.io.Serializable
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    companion object{
        const val CHANNEL_ID= "myChannel"
        var userRoot: User? = null
    }
    private val DP_KEYBOARD: Int = 200
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
        getUser()



        hideNavKeyboard()

        loadFragment(Home(),false)

        setupBottomNavigationView()


        //funciona
        createChannel()

    }
    private fun getUser(){
        val callback = object : Callback.MyCallback<User> {
            override fun onSuccess(data: User) {
                userRoot = data
            }

            override fun onError(errorMsg: User?) {

            }
        }
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val id = preferences.getInt(USER_ID, 0)
        if(id == 0){
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish()
        }else{
            userAPIClient.getUserById(id,callback)
        }



    }

    @SuppressLint("ScheduleExactAlarm")
    fun sheduleNotification(event: Event){
        val dateSplit = event.fecha_inicio.split(",")[0].split("-")

        val year = dateSplit[0].trim().toInt()
        val month = dateSplit[1].trim().toInt() -1
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
            set(year, month, day, /*hora*/ 10, /*minuto*/ 0, /*segundo*/ 0)
        }
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Programa la alarma para la fecha específica
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )


    }
    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
        // Cancelar el alarm pendiente
        val intent = Intent(context, AlarmNotification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
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
                R.id.favorite -> {
                    loadFragment(Favorite(),false)
                    true
                }
                R.id.profile -> {
                    loadFragment(Profile(),false)
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
            if (heightDiff > dpToPx() || isBottomNavVisible) {
                binding.nav.visibility = View.GONE
            } else {
                binding.nav.visibility = View.VISIBLE
            }
        })
    }
    private fun dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (DP_KEYBOARD * density).toInt()
    }
    fun setBottomNavVisibility(visible: Boolean) {
        isBottomNavVisible = visible
    }



}