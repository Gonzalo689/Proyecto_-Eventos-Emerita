package com.example.android_eventosemerita.controller.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.FeedSearchAllBinding


class AdapterSearchAll (private var events: ArrayList<Event>) : RecyclerView.Adapter<AdapterSearchAll.FeedViewAllEvents>() {

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
            binding.textViewSearch.text = event.titulo
        }

    }
}