package com.example.android_eventosemerita

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.EventAPIClient
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bindin: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        bindin = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindin.root)
        val eventAPIClient = EventAPIClient(this)



        val callback = object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
                println(data[0].toString())
                bindin.text.text = data[0].toString()
            }
            override fun onError(errorMsg: String) {
                println("Mal")
                bindin.text.text = "Mal"
                println(errorMsg)
            }
        }
        eventAPIClient.getAllEvents(callback)

    }
}