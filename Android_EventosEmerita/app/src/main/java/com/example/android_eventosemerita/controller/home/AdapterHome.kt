package com.example.android_eventosemerita.controller.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.FeedHomeBinding
import com.example.android_eventosemerita.fragments_nav.FragmentEvent
import com.squareup.picasso.Picasso

/**
 * AdapterHome es un adaptador de RecyclerView que maneja la vista de los elementos de la
 * pantalla principal de la aplicación.
 *
 * @param events Lista de eventos que se mostrarán en la pantalla principal.
 * @param mainActivity Instancia de MainActivity, utilizada para cargar fragmentos y controlar
 * la visibilidad de la barra de navegación inferior.
 */
class AdapterHome (private val events: ArrayList<Event>,private val mainActivity: MainActivity) : RecyclerView.Adapter<AdapterHome.FeedViewHome>() {

    /**
     * onCreateViewHolder se llama cuando se necesita crear una nueva vista de elemento de la
     * pantalla principal.
     * @param parent El ViewGroup en el que se inflará la nueva vista.
     * @param viewType El tipo de la vista, que se utiliza para crear diferentes tipos de vistas
     * de elementos.
     * @return FeedViewHome, una instancia de ViewHolder que contiene la vista de un elemento de
     * la pantalla principal.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHome {
        val binding = FeedHomeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FeedViewHome(binding)
    }

    /**
     * onBindViewHolder se llama cuando se necesita vincular los datos de un evento específico en
     * una posición dada al FeedViewHome correspondiente.
     *
     * @param feedHolder El FeedViewHome en el que se vincularán los datos.
     * @param position La posición del evento en la lista de eventos.
     */
    override fun onBindViewHolder(feedHolder: FeedViewHome, position: Int) {
        feedHolder.bind(events[position])
    }

    /**
     * getItemCount devuelve el número total de elementos en la lista de eventos.
     *
     * @return El número total de elementos en la lista de eventos.
     */
    override fun getItemCount(): Int {
        return events.size
    }

    /**
     * FeedViewHome es una clase interna que actúa como ViewHolder para los elementos de la
     * pantalla principal. Contiene un método bind que se utiliza para vincular los datos de un
     * evento específico a la vista correspondiente.
     *
     * @param binding Binding generado para la vista del elemento de la pantalla principal.
     */
    inner class FeedViewHome(private val binding: FeedHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * bind vincula los datos de un evento específico a la vista correspondiente.
         *
         * @param event Evento que se va a mostrar.
         */
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