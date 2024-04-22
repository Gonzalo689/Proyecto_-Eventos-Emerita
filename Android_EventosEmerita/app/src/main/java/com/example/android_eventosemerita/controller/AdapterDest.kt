package com.example.android_eventosemerita.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.MainActivity
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.FeedDestBinding
import com.example.android_eventosemerita.fragments_nav.FragmentEvent
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class AdapterDest (private val events: ArrayList<Event>,private val mainActivity: MainActivity) : RecyclerView.Adapter<AdapterDest.FeedViewDest>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewDest {

        val binding = FeedDestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewDest(binding)
    }

    override fun onBindViewHolder(feedHolder: FeedViewDest, position: Int) {
        feedHolder.bind(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }

    /**
     * Clase interna FeedViewHome
     */
    inner class FeedViewDest(private val binding: FeedDestBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(event: Event) {
            var descriptionC = ""
            event.descriptionCompleta.forEach { des ->
                descriptionC += des.trim()
            }
            binding.descp.text = descriptionC
            Picasso.get().load(event.imagenIni).into(binding.imageEvent, object : Callback {
                override fun onSuccess() {
                    binding.progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.progressBar.visibility = View.GONE
                }
            })

            binding.layautDest.setOnClickListener {
                val fragmentEvent = FragmentEvent.newInstance(event)
                mainActivity.loadFragment(fragmentEvent)

                //mainActivity.loadFragment(fragmentEvent)

            }
        }
    }
}