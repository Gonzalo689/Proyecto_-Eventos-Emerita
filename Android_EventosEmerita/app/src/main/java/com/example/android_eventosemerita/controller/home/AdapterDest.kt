package com.example.android_eventosemerita.controller.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.FeedDestBinding
import com.example.android_eventosemerita.fragments_nav.FragmentEvent
import com.squareup.picasso.Picasso


/**
 * AdapterDest es un adaptador de RecyclerView que maneja la vista de los elementos de destino
 * (eventos destacados) en la pantalla principal de la aplicación.
 *
 * @param events Lista de eventos destacados.
 * @param mainActivity Instancia de MainActivity, utilizada para cargar fragmentos y controlar
 * la visibilidad de la barra de navegación inferior.
 */
class AdapterDest (private val events: ArrayList<Event>,private val mainActivity: MainActivity) : RecyclerView.Adapter<AdapterDest.FeedViewDest>() {

    /**
     * onCreateViewHolder se llama cuando se necesita crear una nueva vista de elemento de destino.
     *
     * @param parent El ViewGroup en el que se inflará la nueva vista.
     * @param viewType El tipo de la vista, que se utiliza para crear diferentes tipos de vistas
     * de elementos.
     * @return FeedViewDest, una instancia de ViewHolder que contiene la vista de un elemento de
     * destino.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewDest {

        val binding = FeedDestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewDest(binding)
    }

    /**
     * onBindViewHolder se llama cuando se necesita vincular los datos de un evento destacado
     * específico en una posición dada al FeedViewDest correspondiente.
     *
     * @param feedHolder El FeedViewDest en el que se vincularán los datos.
     * @param position La posición del elemento de destino en la lista de eventos.
     */
    override fun onBindViewHolder(feedHolder: FeedViewDest, position: Int) {
        feedHolder.bind(events[position])
    }

    /**
     * getItemCount devuelve el número total de elementos en la lista de eventos destacados.
     *
     * @return El número total de elementos en la lista de eventos destacados.
     */
    override fun getItemCount(): Int {
        return events.size
    }

    /**
     * FeedViewDest es una clase interna que actúa como ViewHolder para los elementos de destino.
     * Contiene un método bind que se utiliza para vincular los datos de un evento destacado
     * específico a la vista correspondiente.
     *
     * @param binding Binding generado para la vista del elemento de destino.
     */
    inner class FeedViewDest(private val binding: FeedDestBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * bind vincula los datos de un evento destacado específico a la vista correspondiente.
         *
         * @param event Evento destacado que se va a mostrar.
         */
        fun bind(event: Event) {
            var descriptionC = ""
            event.descriptionCompleta.forEach { des ->
                descriptionC += des.trim() + "\n"
            }
            binding.descp.text = event.titulo
            Picasso.get().load(event.image).into(binding.imageEvent)

            binding.layautDest.setOnClickListener {
                val fragmentEvent = FragmentEvent.newInstance(event)
                mainActivity.loadFragment(fragmentEvent,true)
                mainActivity.setBottomNavVisibility(true)
            }
        }

    }
}