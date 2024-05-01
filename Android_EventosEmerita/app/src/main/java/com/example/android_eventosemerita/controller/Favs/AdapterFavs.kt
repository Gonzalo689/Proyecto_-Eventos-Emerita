package com.example.android_eventosemerita.controller.Favs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.FeedFavsBinding
import com.example.android_eventosemerita.fragments_nav.FragmentEvent
import com.squareup.picasso.Picasso

class AdapterFavs(private val events: ArrayList<Event>, private val mainActivity: MainActivity) : RecyclerView.Adapter<AdapterFavs.FeedViewFavs>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewFavs {
        val binding = FeedFavsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewFavs(binding)
    }

    override fun onBindViewHolder(feedHolder: FeedViewFavs, position: Int) {
        feedHolder.bind(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }

    /**
     * Clase interna FeedViewFavs
     */
    inner class FeedViewFavs(private val binding: FeedFavsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
//            var descriptionC = ""
//            event.descriptionCompleta.forEach { des ->
//                descriptionC += des.trim()
//            }
//            binding.descp.text = descriptionC
            Picasso.get().load(event.imagenIni).into(binding.imageView)
//
//            binding.layautDest.setOnClickListener {
//                val fragmentEvent = FragmentEvent.newInstance(event)
//                mainActivity.loadFragment(fragmentEvent,true)
//                mainActivity.setBottomNavVisibility(true)
//            }
        }

    }
}