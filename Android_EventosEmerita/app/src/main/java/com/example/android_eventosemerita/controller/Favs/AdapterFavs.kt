package com.example.android_eventosemerita.controller.Favs

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.R
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

            Picasso.get().load(event.imagenIni).into(binding.imageView)

            binding.title.text = event.titulo

            binding.date.text = event.stringFecha(event.fecha_inicio)
            binding.addres.text = event.direccion

            var gradientDrawable: Drawable? = null
            val dateNum = event.checkDate(event.fecha_inicio)
            if (dateNum == -1) {
                gradientDrawable = ContextCompat.getDrawable(mainActivity, R.drawable.card_favs_past)
                binding.layautInfo.visibility = View.VISIBLE
            }
            if (dateNum ==0){
                gradientDrawable = ContextCompat.getDrawable(mainActivity, R.drawable.card_favs_today)
                binding.layautInfo.visibility = View.VISIBLE
                binding.textInfo.text = "HOY"

            }

            binding.imageView.foreground = gradientDrawable

            binding.cardFav.setOnClickListener {
                val fragmentEvent = FragmentEvent.newInstance(event)
                mainActivity.loadFragment(fragmentEvent,true)
                mainActivity.setBottomNavVisibility(true)
            }
        }

    }
}