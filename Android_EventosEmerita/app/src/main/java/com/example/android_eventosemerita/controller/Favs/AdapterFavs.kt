package com.example.android_eventosemerita.controller.Favs

import android.content.Context
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
import java.util.Calendar

/**
 * AdapterFavs es una clase adaptadora para mostrar la lista de eventos favoritos en un RecyclerView.
 * Esta clase se encarga de vincular los datos de los eventos favoritos con la vista de cada elemento de la lista.
 *
 * @property events La lista de eventos favoritos que se mostrarán.
 * @property mainActivity La actividad principal que contiene este RecyclerView.
 */
class AdapterFavs(private val events: ArrayList<Event>, private val mainActivity: MainActivity) : RecyclerView.Adapter<AdapterFavs.FeedViewFavs>() {

    /**
     *  Creación de la vista de cada elemento de la lista
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewFavs {
        val binding = FeedFavsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewFavs(binding, parent.context)
    }

    /**
     *  Vinculación de datos a la vista de cada elemento de la lista
     */
    override fun onBindViewHolder(feedHolder: FeedViewFavs, position: Int) {
        feedHolder.bind(events[position])
    }

    /**
     *  Devuelve la cantidad de elementos en la lista
     */
    override fun getItemCount(): Int {
        return events.size
    }

    /**
     * Clase interna FeedViewFavs, responsable de vincular los datos de un evento con la vista de un elemento de la lista.
     */
    inner class FeedViewFavs(private val binding: FeedFavsBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            val calendar = Calendar.getInstance()
            val yearNow = calendar.get(Calendar.YEAR)
            val monthNow = calendar.get(Calendar.MONTH) + 1
            val dayNow = calendar.get(Calendar.DAY_OF_MONTH)

            val datenow= "$yearNow-$monthNow-$dayNow"


            Picasso.get().load(event.imagenIni).into(binding.imageView)

            binding.title.text = event.titulo

            binding.date.text = event.stringFecha(event.fecha_inicio)
            binding.addres.text = event.direccion

            var gradientDrawable: Drawable? = null
            val dateNum = event.checkDate(datenow)
            if (dateNum == -1) {
                gradientDrawable = ContextCompat.getDrawable(mainActivity, R.drawable.card_favs_past)
                binding.layautInfo.visibility = View.VISIBLE
            }
            if (dateNum ==0){
                gradientDrawable = ContextCompat.getDrawable(mainActivity, R.drawable.card_favs_today)
                binding.layautInfo.visibility = View.VISIBLE
                binding.textInfo.text = context.getString(R.string.in_progress)

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