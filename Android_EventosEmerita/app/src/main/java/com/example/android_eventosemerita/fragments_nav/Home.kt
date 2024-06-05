package com.example.android_eventosemerita.fragments_nav

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.activity.SplashScreen
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.EventAPIClient
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.controller.home.AdapterDest
import com.example.android_eventosemerita.controller.home.AdapterHome
import com.example.android_eventosemerita.databinding.FragmentHomeBinding
import com.example.android_eventosemerita.utils.UtilsConst.userRoot

/**
 * Fragmento para la pantalla de inicio que muestra diferentes listas de eventos.
 */
class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var eventAPIClient: EventAPIClient
    private lateinit var userAPIClient: UserAPIClient
    private var retryCountAll = 0
    private var retryCountDest = 0
    private var retryCountRecomend = 0
    private var retryCountPast = 0
    private var retryCountWeeend = 0

    private val maxRetries = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        userAPIClient = UserAPIClient(requireContext())
        eventAPIClient = EventAPIClient(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (userRoot != null){
            eventRecomends()
        }else{
            binding.recyclerRecomend.visibility = View.GONE
            binding.textRecomend.visibility = View.GONE
            binding.progressRecomend.visibility = View.GONE
        }
        eventsDestAPI()
        eventsAllAPI()
        eventPastDest()
        eventWeekend()

    }

    /**
     * Obtiene y muestra la lista de todos los eventos.
     */
    private fun eventsAllAPI() {
        eventAPIClient.getAllEvents(object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
                binding.progressNew.visibility = View.GONE
                retryCountAll = 0
                val sublist: List<Event> = if (data.size > 10) {
                    data.reversed().subList(0, 10)
                } else {
                    data.reversed()
                }
                val eventsDest: ArrayList<Event> = ArrayList(sublist)
                recyclerall(eventsDest, binding.recyclerNew)


            }

            override fun onError(errorMsg: List<Event>?) {
                reloadError(retryCountAll, maxRetries) { eventsAllAPI() }
                retryCountAll++

            }
        })
    }

    /**
     * Obtiene y muestra la lista de eventos recomendados para el usuario actual.
     */
    fun eventRecomends(){
        userAPIClient.getRecomendList(userRoot!!.id, object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>){
                binding.progressRecomend.visibility = View.GONE
                if (data.isNotEmpty()){
                    retryCountRecomend = 0
                    val eventsDest: ArrayList<Event> = ArrayList(data)
                    recyclerall(eventsDest,binding.recyclerRecomend)
                }
            }

            override fun onError(errorMsg: List<Event>?) {
                reloadError(retryCountRecomend, maxRetries) { eventRecomends() }
                retryCountRecomend++
            }
        })
    }

    /**
     * Obtiene y muestra la lista de eventos pasados.
     */
    fun eventPastDest(){
        eventAPIClient.getEventsDestPast(object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
                binding.progressPastDest.visibility = View.GONE
                retryCountPast = 0
                val eventsDest: ArrayList<Event> = ArrayList(data)
                recyclerall(eventsDest, binding.recyclerPast)
            }

            override fun onError(errorMsg: List<Event>?) {
                reloadError(retryCountPast, maxRetries) { eventPastDest() }
                retryCountPast++

            }
        })
    }

    /**
     * Obtiene y muestra la lista de eventos para el fin de semana.
     */
    fun eventWeekend(){
        eventAPIClient.getWeekend(object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
                binding.progressWeekend.visibility = View.GONE
                retryCountWeeend = 0
                val eventsDest: ArrayList<Event> = ArrayList(data)
                recyclerall(eventsDest, binding.recyclerWeekend)


            }

            override fun onError(errorMsg: List<Event>?) {
                reloadError(retryCountWeeend, maxRetries) { eventWeekend() }
                retryCountWeeend++

            }
        })
    }

    /**
     * Obtiene y muestra la lista de eventos destacados.
     */
    fun eventsDestAPI(){
        eventAPIClient.getEventsDest(object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
                binding.progressDest.visibility = View.GONE
                retryCountDest = 0
                if (data.isNotEmpty()) {
                    val eventsDest: ArrayList<Event> = ArrayList(data)
                    recyclerDest(eventsDest)
                }
            }
            override fun onError(errorMsg: List<Event>?) {
                reloadError(retryCountDest, maxRetries) { eventsDestAPI() }
                retryCountDest++

            }
        })
    }

    /**
     * Regresa a la pantalla de inicio (SplashScreen) si hay un error de carga.
     */
    fun returnToSplash(){
        val intent = Intent(requireContext(), SplashScreen::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    /**
     * Recarga la pantalla o regresa a la pantalla de inicio si se ha intentado cargar varias veces sin éxito.
     */
    fun reloadError(retryCount: Int, maxRetries: Int, callback: () -> Unit) {
        if (retryCount < maxRetries) {
            callback()
        } else {
            returnToSplash()
        }
    }

    /**
     * Configura el RecyclerView para mostrar la lista de eventos destacados.
     */
    fun recyclerDest(eventsList: ArrayList<Event>){
        if (!isAdded) {
            return
        }
        val mainActivity = requireActivity() as MainActivity
        val adapter = AdapterDest(eventsList, mainActivity)
        binding.recyclerDest.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerDest.layoutManager = layoutManager
    }

    /**
     * Configura el RecyclerView para cualquier lista en su respectivo recycler.
     */
    fun recyclerall(eventsList: ArrayList<Event> , recyclerView: RecyclerView){
        if (!isAdded) {
            return
        }
        val mainActivity = requireActivity() as MainActivity
        val adapter = AdapterHome(eventsList,mainActivity)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
    }

}