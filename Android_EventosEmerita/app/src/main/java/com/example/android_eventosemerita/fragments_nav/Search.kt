package com.example.android_eventosemerita.fragments_nav

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.EventAPIClient
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.FragmentSearchBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.model.Category
import com.example.android_eventosemerita.controller.search.AdapterCategory
import com.example.android_eventosemerita.controller.search.AdapterSearchAll
import java.util.Calendar


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
            binding.button.visibility = View.GONE
        })
        binding.textCategory.setOnClickListener(View.OnClickListener {
            binding.recyclerSearch.visibility = View.GONE
            binding.recyclerSearchCategory.visibility = View.VISIBLE
        })
        binding.buttonDate.setOnClickListener(View.OnClickListener {
            filterEventsDate()
        })
        binding.textDate.setOnClickListener(View.OnClickListener {
            binding.textDate.visibility = View.GONE
            eventsAPI()
            filterEvents(binding.searchView.query.toString())
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
            override fun onError(errorMsg:  List<Event>?) {
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
                    allEventsList.addAll(data.reversed())
                    adapter.updateEvents(allEventsList)
                }
            }

            override fun onError(errorMsg: List<Event>?) {

            }
        }
        eventAPIClient.getEventCategory(category,callback)

        binding.recyclerSearchCategory.visibility = View.GONE
        binding.recyclerSearch.visibility = View.VISIBLE

        //todo, no funciona
        filterEvents( binding.searchView.query.toString())

    }

    private fun filterEvents(query: String) {
        var filteredEvents = ArrayList<Event>()
        if (!query.isEmpty()){
            binding.recyclerSearchCategory.visibility = View.GONE
            for (event in allEventsList) {
                if (event.titulo.contains(query, true)) {
                    filteredEvents.add(event)
                }
            }
            filteredEvents.reversed()
        }else{
            if (!binding.button.isVisible){
                eventsAPI()
            }
            filteredEvents = ArrayList(allEventsList)
        }

        adapter.updateEvents(filteredEvents)
    }
    private fun filterEventsDate() {
        val filteredEvents = ArrayList<Event>()
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->

                val selectedDate = "$selectedYear-${selectedMonth+1}-$selectedDay"
                println("Fecha "+selectedDate)
                binding.recyclerSearchCategory.visibility = View.GONE
                binding.recyclerSearch.visibility = View.VISIBLE
                for (event in allEventsList) {
                    if (event.checkDate(selectedDate) == 0) {
                        filteredEvents.add(event)
                    }
                }
                filteredEvents.reversed()
                binding.textDate.text = selectedDate
                binding.textDate.visibility = View.VISIBLE
                adapter.updateEvents(filteredEvents)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
    private fun adapterAllEvents(){
        val mainActivity = requireActivity() as MainActivity
        adapter = AdapterSearchAll(ArrayList(),mainActivity)
        binding.recyclerSearch.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerSearch.layoutManager = layoutManager
    }
    private fun adapterCategory(){
        adapterCategory = AdapterCategory(categories , this, binding.button)
        binding.recyclerSearchCategory.adapter = adapterCategory
        val layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        binding.recyclerSearchCategory.layoutManager = layoutManager
    }

    private fun searchQuery(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterEvents(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterEvents(it) }
                return true
            }
        })
    }
    private fun chargeCategories(){
        categories.add(Category("Benéfico", "Benéfico", R.drawable.beneficial))
        categories.add(Category("Diseño", "Diseño", R.drawable.design))
        categories.add(Category("Carnaval", "Carnaval", R.drawable.carnival))
        categories.add(Category("Música", "Música", R.drawable.music))
        categories.add(Category("Conferencia", "Charla-Conferencia", R.drawable.conference))
        //categories.add(Category("Jornada", "Jornadas", R.drawable.prueba))
        categories.add(Category("Cine", "Cine", R.drawable.cinema))
        categories.add(Category("Talleres", "Curso-Taller", R.drawable.workshops))
        categories.add(Category("Emerita Lvdica", "Emerita Lvdica", R.drawable.emerita_lvdica))
        categories.add(Category("Danza", "Danza", R.drawable.dance))
        categories.add(Category("Deporte", "Deporte", R.drawable.sport))
        categories.add(Category("Infantil", "Infantil", R.drawable.childish))
        categories.add(Category("Ocio", "Ocio", R.drawable.leisure))
        categories.add(Category("Teatro", "Teatro", R.drawable.theater))
        categories.add(Category("Literatura", "Literatura", R.drawable.literature))
        //categories.add(Category("Visita Guiada", "Visita Guiada", R.drawable.prueba))

    }





}