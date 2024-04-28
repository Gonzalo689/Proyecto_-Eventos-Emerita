package com.example.android_eventosemerita.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.EventAPIClient
import com.example.android_eventosemerita.databinding.ActivitySplashScreenBinding
import com.example.android_eventosemerita.login.SignIn
import com.example.android_eventosemerita.login.SignIn.Companion.REMEMBER
import com.example.android_eventosemerita.login.SignIn.Companion.USER_ID


class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var eventAPIClient: EventAPIClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageView2.setImageResource(R.drawable.prueba)

        eventAPIClient = EventAPIClient(this)
        makeApiCall()

//        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
//        val editor = preferences.edit()
//        editor.putBoolean(REMEMBER, false)
//        editor.putInt(USER_ID, 0)
//        editor.apply()

    }

    private fun makeApiCall() {
        val callback = object : Callback.MyCallback<String> {
            override fun onSuccess(data: String) {
                if (data.isNotEmpty() && data.equals("conect")) {
                    navigateToMain()
                }else{
                    Handler(mainLooper).postDelayed({ makeApiCall()}, 2000)
                }
            }

            override fun onError(errorMsg: String) {
                Handler(mainLooper).postDelayed({ makeApiCall() }, 2000)
            }
        }
        eventAPIClient.getConexion(callback)
    }

    private fun navigateToMain() {
        val intent = Intent(this, SignIn::class.java)
        startActivity(intent)
        finish()
    }


}