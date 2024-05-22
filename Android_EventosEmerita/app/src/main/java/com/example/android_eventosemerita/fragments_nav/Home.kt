package com.example.android_eventosemerita.fragments_nav

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.activity.SplashScreen
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.EventAPIClient
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.controller.home.AdapterDest
import com.example.android_eventosemerita.controller.home.AdapterHome
import com.example.android_eventosemerita.databinding.FragmentHomeBinding

class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var eventAPIClient: EventAPIClient
    private var retryCountAll = 0
    private var retryCountDest = 0
    private val maxRetries = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        eventAPIClient = EventAPIClient(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventsDestAPI()
        eventsAllAPI()

    }

    private fun eventsAllAPI() {
        val callback = object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
                retryCountAll = 0
                val sublist: List<Event> = if (data.size > 10) {
                    data.reversed().subList(0, 10)
                } else {
                    data.reversed()
                }
                val eventsDest: ArrayList<Event> = ArrayList(sublist)
                recyclerall(eventsDest)

            }

            override fun onError(errorMsg: List<Event>?) {
                if (retryCountAll < maxRetries) {
                    retryCountAll++
                    eventAPIClient.getAllEvents(this)
                } else {
                    if (isAdded) {
                        returnToSplash()
                    }
                }

            }
        }
            eventAPIClient.getAllEvents(callback)
    }
    fun returnToSplash(){
        val intent = Intent(requireContext(), SplashScreen::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    fun eventsDestAPI(){
        val callback = object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
                retryCountDest = 0
                if (data.isNotEmpty()) {
                    val sublist: List<Event> = if (data.size > 10) {
                        data.subList(0, 10)
                    } else {
                        data
                    }
                    val eventsDest: ArrayList<Event> = ArrayList(sublist)
                    recyclerDest(eventsDest)
                }
            }
            override fun onError(errorMsg: List<Event>?) {
                if (retryCountDest < maxRetries) {
                    retryCountDest++
                    eventAPIClient.getEventsDest(this)
                } else {
                    if (isAdded) {
                        returnToSplash()
                    }
                }

            }
        }

        eventAPIClient.getEventsDest(callback)
    }
    fun recyclerDest(eventsList: ArrayList<Event>){
        if (!isAdded) {
            return
        }
        val mainActivity = requireActivity() as MainActivity
        val adapter = AdapterDest(eventsList, mainActivity)
        binding.recyclerDest.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerDest.layoutManager = layoutManager
    }
    fun recyclerall(eventsList: ArrayList<Event>){
        if (!isAdded) {
            return
        }
        val mainActivity = requireActivity() as MainActivity
        val adapter = AdapterHome(eventsList,mainActivity)
        binding.recyclerNew.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerNew.layoutManager = layoutManager
    }

}