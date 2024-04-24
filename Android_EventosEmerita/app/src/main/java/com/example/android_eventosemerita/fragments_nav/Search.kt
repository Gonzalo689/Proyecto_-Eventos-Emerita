package com.example.android_eventosemerita.fragments_nav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.EventAPIClient
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.FragmentSearchBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.api.model.Category
import com.example.android_eventosemerita.controller.search.AdapterCategory
import com.example.android_eventosemerita.controller.search.AdapterSearchAll



class Search : Fragment(){

    private lateinit var binding: FragmentSearchBinding
    private lateinit var eventAPIClient: EventAPIClient
    private lateinit var adapter: AdapterSearchAll
    private lateinit var adapterCategory: AdapterCategory
    private var allEventsList: ArrayList<Event> = ArrayList()
    private var categories: ArrayList<Category> = ArrayList()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        eventAPIClient = EventAPIClient(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chargeCategories()
        adapterCategory()

        adapterAllEvents()
        searchQuery()

        eventsAPI()

        binding.button.setOnClickListener(View.OnClickListener {
            eventsAPI()
            binding.searchView.setQuery("", false)
            filterEvents("")
        })


    }
    fun eventsAPI(){
        val callback = object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
                if (data.isNotEmpty()) {
                    allEventsList.clear()
                    allEventsList.addAll(data)
                }
            }
            override fun onError(errorMsg: String) {
                println("Error: $errorMsg")
            }
        }
        eventAPIClient.getAllEvents(callback)
    }
    fun EventsCategory(category:String){
        val callback = object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
                if (data.isNotEmpty()) {
                    allEventsList.clear()
                    allEventsList.addAll(data)
                    adapter.updateEvents(allEventsList)
                }
            }
            override fun onError(errorMsg: String) {
                println("Error: $errorMsg")
            }
        }
        eventAPIClient.getEventCategory(category,callback)
        binding.recyclerSearchCategory.visibility = View.GONE
        binding.recyclerSearch.visibility = View.VISIBLE

    }

    private fun filterEvents(query: String) {
        val filteredEvents = ArrayList<Event>()
        if (!query.isEmpty()){
            binding.recyclerSearchCategory.visibility = View.GONE
            for (event in allEventsList) {
                if (event.titulo.contains(query, true)) {
                    filteredEvents.add(event)
                }
            }
        }else{
            binding.recyclerSearchCategory.visibility = View.VISIBLE
        }

        adapter.updateEvents(filteredEvents)
    }
    private fun adapterAllEvents(){
        adapter = AdapterSearchAll(ArrayList())
        binding.recyclerSearch.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerSearch.layoutManager = layoutManager
    }
    private fun adapterCategory(){
        adapterCategory = AdapterCategory(categories , this)
        binding.recyclerSearchCategory.adapter = adapterCategory
        val layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        binding.recyclerSearchCategory.layoutManager = layoutManager
    }

    private fun searchQuery(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Lógica para manejar la sumisión del texto de búsqueda
                query?.let { filterEvents(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Lógica para manejar los cambios en el texto de búsqueda
                newText?.let { filterEvents(it) }
                return true
            }
        })
    }
    private fun chargeCategories(){
        categories.add(Category("Benéfico", "Benéfico", R.drawable.prueba))
        categories.add(Category("Diseño", "Diseño", R.drawable.prueba))
        categories.add(Category("Carnaval", "Carnaval", R.drawable.prueba))
        categories.add(Category("Música", "Música", R.drawable.prueba))
        categories.add(Category("Conferencia", "Charla-Conferencia", R.drawable.prueba))
        categories.add(Category("Jornada", "Jornadas", R.drawable.prueba))
        categories.add(Category("Cine", "Cine", R.drawable.prueba))
        categories.add(Category("Talleres", "Curso-Taller", R.drawable.prueba))
        categories.add(Category("Emerita Lvdica", "Emerita Lvdica", R.drawable.prueba))
        categories.add(Category("Danza", "Danza", R.drawable.prueba))
        categories.add(Category("Deporte", "Deporte", R.drawable.prueba))
        categories.add(Category("Recreación", "Recreación", R.drawable.prueba))
        categories.add(Category("Feria", "Feria", R.drawable.prueba))
        categories.add(Category("Infantil", "Infantil", R.drawable.prueba))




    }





}