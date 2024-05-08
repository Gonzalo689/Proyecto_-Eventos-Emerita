package com.example.android_eventosemerita.controller.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.FeedSearchAllBinding
import com.example.android_eventosemerita.fragments_nav.FragmentEvent
import com.squareup.picasso.Picasso


class AdapterSearchAll (private var events: ArrayList<Event>, private val mainActivity: MainActivity) : RecyclerView.Adapter<AdapterSearchAll.FeedViewAllEvents>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewAllEvents {

        val binding = FeedSearchAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewAllEvents(binding)
    }

    override fun onBindViewHolder(feedHolder: FeedViewAllEvents, position: Int) {
        feedHolder.bind(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }
    fun updateEvents(newEventsList: List<Event>) {
        events = newEventsList as ArrayList<Event>
        notifyDataSetChanged()
    }
    inner class FeedViewAllEvents(private val binding: FeedSearchAllBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            Picasso.get().load(event.image).into(binding.image)
            binding.title.text = event.titulo
            binding.address.text = event.direccion
            binding.date.text = event.fecha_inicio
            binding.layautFeedSearch.setOnClickListener(View.OnClickListener {
                val fragmentEvent = FragmentEvent.newInstance(event)
                mainActivity.loadFragment(fragmentEvent,true)
                mainActivity.setBottomNavVisibility(true)
            })
        }

    }
}