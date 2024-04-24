package com.example.android_eventosemerita.fragments_nav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.EventAPIClient
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.controller.home.AdapterDest
import com.example.android_eventosemerita.databinding.FragmentHomeBinding

class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var eventAPIClient: EventAPIClient

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

        eventsAPI()
    }

    fun eventsAPI(){

        val callback = object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
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
            override fun onError(errorMsg: String) {
                println("Error: $errorMsg")
            }
        }

        eventAPIClient.getEventsDest(callback)
    }
    fun recyclerDest(eventsList: ArrayList<Event>){
        val mainActivity = requireActivity() as MainActivity
        val adapter = AdapterDest(eventsList, mainActivity)
        binding.recyclerDest.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerDest.layoutManager = layoutManager
    }

}