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

/**
 * AdapterSearchAll es un adaptador de RecyclerView que maneja la vista de todos los eventos
 * mostrados en la pantalla de búsqueda.
 *
 * @param events Lista de eventos que se mostrarán en la pantalla de búsqueda.
 * @param mainActivity Instancia de MainActivity, utilizada para cargar fragmentos y controlar
 * la visibilidad de la barra de navegación inferior.
 */
class AdapterSearchAll (private var events: ArrayList<Event>, private val mainActivity: MainActivity) : RecyclerView.Adapter<AdapterSearchAll.FeedViewAllEvents>() {

    /**
     * onCreateViewHolder se llama cuando se necesita crear una nueva vista de elemento de la lista
     * de eventos.
     *
     * @param parent El ViewGroup en el que se inflará la nueva vista.
     * @param viewType El tipo de la vista, que se utiliza para crear diferentes tipos de vistas
     * de elementos.
     * @return FeedViewAllEvents, una instancia de ViewHolder que contiene la vista de un elemento
     * de evento.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewAllEvents {

        val binding = FeedSearchAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewAllEvents(binding)
    }

    /**
     * onBindViewHolder se llama cuando se necesita vincular los datos de un evento específico en
     * una posición dada al FeedViewAllEvents correspondiente.
     *
     * @param feedHolder El FeedViewAllEvents en el que se vincularán los datos.
     * @param position La posición del evento en la lista de eventos.
     */
    override fun onBindViewHolder(feedHolder: FeedViewAllEvents, position: Int) {
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
     * updateEvents actualiza la lista de eventos y notifica al adaptador que los datos han
     * cambiado.
     *
     * @param newEventsList Nueva lista de eventos que se mostrarán en la pantalla de búsqueda.
     */
    fun updateEvents(newEventsList: List<Event>) {
        events = newEventsList as ArrayList<Event>
        notifyDataSetChanged()
    }

    /**
     * FeedViewAllEvents es una clase interna que actúa como ViewHolder para los elementos de la
     * lista de eventos. Contiene un método bind que se utiliza para vincular los datos de un
     * evento específico a la vista correspondiente.
     *
     * @param binding Binding generado para la vista del elemento de la lista de eventos.
     */
    inner class FeedViewAllEvents(private val binding: FeedSearchAllBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * bind vincula los datos de un evento específico a la vista correspondiente.
         *
         * @param event Evento que se va a mostrar.
         */
        fun bind(event: Event) {
            Picasso.get().load(event.image).into(binding.image)
            binding.title.text = event.titulo
            binding.address.text = event.direccion
            binding.date.text = editDate(event)
            binding.layautFeedSearch.setOnClickListener(View.OnClickListener {
                val fragmentEvent = FragmentEvent.newInstance(event)
                mainActivity.loadFragment(fragmentEvent,true)
                mainActivity.setBottomNavVisibility(true)
            })
        }

        /**
         * Función que devuelve la fecha más legible
         */
        fun editDate(event: Event):String{
            val hour = event.fecha_inicio.split(",")[1].split(":")
            val fecha = event.stringFecha(event.fecha_inicio) + ", " + hour[0]+":" + hour[1]
            return fecha
        }

    }
}