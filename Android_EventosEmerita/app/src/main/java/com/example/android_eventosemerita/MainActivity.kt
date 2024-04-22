package com.example.android_eventosemerita

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.EventAPIClient
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.ActivityMainBinding
import com.example.android_eventosemerita.fragments_nav.Home

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventAPIClient = EventAPIClient(this)
        val callback = object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
                println(data[0].toString())
                //bindin.text.text = data[0].toString()
            }
            override fun onError(errorMsg: String) {
                println("Mal")
                //bindin.text.text = "Mal"
                println(errorMsg)
            }
        }
        eventAPIClient.getAllEvents(callback)
        loadFragment(Home.newInstance("param1", "param2"))
        setupBottomNavigationView()

    }
    private fun setupBottomNavigationView() {
        binding.nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(Home.newInstance("param1", "param2"))
                    true
                }
                R.id.search -> {
                    loadFragment(Home.newInstance("param1", "param2"))
                    true
                }
                // Agrega más casos si tienes más elementos en el BottomNavigationView
                else -> false
            }
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }
}