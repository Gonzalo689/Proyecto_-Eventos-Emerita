package com.example.android_eventosemerita.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.FeedDestBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class AdapterDest (private val events: ArrayList<Event>) : RecyclerView.Adapter<AdapterDest.FeedViewDest>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewDest {

        //val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_dest, parent, false)
        //return FeedViewDest(view)
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
            binding.descp.text = event.titulo
            Picasso.get().load(event.imagenIni).into(binding.imageEvent)
            Picasso.get().load(event.imagenIni).into(binding.imageEvent, object : Callback {
                override fun onSuccess() {
                    binding.progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.progressBar.visibility = View.GONE
                }
            })

            binding.layautDest.setOnClickListener {
                binding.descp.text = "Nuevo texto"
            }
        }
    }
}