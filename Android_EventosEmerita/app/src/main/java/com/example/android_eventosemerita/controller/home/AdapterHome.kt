package com.example.android_eventosemerita.controller.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.FeedHomeBinding
import com.example.android_eventosemerita.fragments_nav.FragmentEvent
import com.squareup.picasso.Picasso

class AdapterHome (private val events: ArrayList<Event>,private val mainActivity: MainActivity) : RecyclerView.Adapter<AdapterHome.FeedViewHome>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHome {
        val binding = FeedHomeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FeedViewHome(binding)
    }

    override fun onBindViewHolder(feedHolder: FeedViewHome, position: Int) {
        feedHolder.bind(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }

    /**
     * Clase interna FeedViewHome
     */
    inner class FeedViewHome(private val binding: FeedHomeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            Picasso.get().load(event.image).into(binding.imageView)

            binding.title.text = event.titulo

            binding.date.text = event.stringFecha(event.fecha_inicio)
            binding.addres.text = event.direccion

            binding.cardFav.setOnClickListener {
                val fragmentEvent = FragmentEvent.newInstance(event)
                mainActivity.loadFragment(fragmentEvent,true)
                mainActivity.setBottomNavVisibility(true)
            }
        }

    }
}