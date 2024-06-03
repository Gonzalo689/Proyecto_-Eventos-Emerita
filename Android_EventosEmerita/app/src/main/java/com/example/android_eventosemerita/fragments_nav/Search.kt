package com.example.android_eventosemerita.fragments_nav

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
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

    companion object{
        var categoryPair = Pair(false,"")
        var datePair = Pair(false,"")
    }
    private lateinit var binding: FragmentSearchBinding
    private lateinit var eventAPIClient: EventAPIClient
    private lateinit var adapter: AdapterSearchAll
    private lateinit var adapterCategory: AdapterCategory
    private var allEventsList: ArrayList<Event> = ArrayList()
    private var categories: ArrayList<Category> = ArrayList()
    private var allEventsListConst: ArrayList<Event> = ArrayList()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        eventAPIClient = EventAPIClient(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inc()
        chargeCategories()
        adapterCategory()

        adapterAllEvents()
        searchQuery()
        eventsAPI()
        buttons()

    }
    private fun buttons(){
        binding.button.setOnClickListener {
            binding.button.visibility = View.GONE
            checkDate()
            categoryPair = Pair(false,"")
        }
        binding.textCategory.setOnClickListener {

            binding.recyclerSearchCategory.visibility = View.VISIBLE
        }
        binding.buttonDate.setOnClickListener {
            if (datePair.first){
                binding.textDate.visibility = View.GONE
                checkCategory()
                datePair = Pair(false,"")
            }
            intentEventsDate()
        }
        binding.textDate.setOnClickListener{
            binding.textDate.visibility = View.GONE
            checkCategory()
            datePair = Pair(false,"")
        }

    }
    private fun inc(){
        binding.textDate.visibility = View.GONE
        binding.button.visibility = View.GONE
        categoryPair = Pair(false,"")
        datePair = Pair(false,"")
    }
    private fun checkDate(){
        val filter = binding.searchView.query.toString()
        reloadEvents()
        if (datePair.first) {
            filterEventsDate(datePair.second)
        }else{
            adapter.updateEvents(allEventsList)
            if (filter.isEmpty()){
                filterEvents(binding.searchView.query.toString())
            }
        }
    }
    private fun checkCategory(){
        val filter = binding.searchView.query.toString()
        reloadEvents()
        if (categoryPair.first) {
            eventsCategory(categoryPair.second)
        }else {
            adapter.updateEvents(allEventsList)
            if (filter.isEmpty()){
                filterEvents(binding.searchView.query.toString())
            }

        }
    }
    private fun reloadEvents(){
        allEventsList.clear()
        allEventsList.addAll(allEventsListConst)
    }
    fun eventsAPI(){
        val callback = object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
                if (data.isNotEmpty()) {
                    if(allEventsListConst.isEmpty()){
                        allEventsListConst.addAll(data)
                    }
                    allEventsList.clear()
                    allEventsList.addAll(data)
                    filterEvents(binding.searchView.query.toString())
                }
            }
            override fun onError(errorMsg:  List<Event>?) {
                println("Error: $errorMsg")
            }
        }
        eventAPIClient.getAllEvents(callback)
    }
    fun eventsCategory(category:String){
        val callback = object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>) {
                if (data.isNotEmpty()) {
                    allEventsList.clear()
                    allEventsList.addAll(data)
                    filterEvents(binding.searchView.query.toString())
                    binding.recyclerSearchCategory.visibility = View.GONE
                    if (datePair.first) {
                        filterEventsDate(datePair.second)
                    }
                }
            }

            override fun onError(errorMsg: List<Event>?) {

            }
        }
        eventAPIClient.getEventCategory(category,callback)


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
        }else{
            filteredEvents = ArrayList(allEventsList)
        }
        filteredEvents.reversed()

        adapter.updateEvents(filteredEvents)
    }
    private fun intentEventsDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth+1}-$selectedDay"
                filterEventsDate(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()

    }

    private fun filterEventsDate(datePersonalized:String){
        val filteredEvents = ArrayList<Event>(allEventsList)
        allEventsList.clear()
        binding.recyclerSearchCategory.visibility = View.GONE
        for (event in filteredEvents) {
            if (event.checkDate(datePersonalized) == 0) {
                allEventsList.add(event)
            }
        }
        filterEvents(binding.searchView.query.toString())
        datePair = Pair(true,datePersonalized)
        binding.textDate.text = datePersonalized
        binding.textDate.visibility = View.VISIBLE
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
        categories.clear()
        categories.add(Category(requireContext().getString(R.string.category_beneficial), "Benéfico", R.drawable.beneficial))
        categories.add(Category(requireContext().getString(R.string.category_design), "Diseño", R.drawable.design))
        categories.add(Category(requireContext().getString(R.string.category_carnival), "Carnaval", R.drawable.carnival))
        categories.add(Category(requireContext().getString(R.string.category_music), "Música", R.drawable.music))
        categories.add(Category(requireContext().getString(R.string.category_conference), "Charla-Conferencia", R.drawable.conference))
        //categories.add(Category("Jornada", "Jornadas", R.drawable.prueba))
        categories.add(Category(requireContext().getString(R.string.category_cinema), "Cine", R.drawable.cinema))
        categories.add(Category(requireContext().getString(R.string.category_workshops), "Curso-Taller", R.drawable.workshops))
        categories.add(Category(requireContext().getString(R.string.category_emerita_lvdica), "Emerita Lvdica", R.drawable.emerita_lvdica))
        categories.add(Category(requireContext().getString(R.string.category_dance), "Danza", R.drawable.dance))
        categories.add(Category(requireContext().getString(R.string.category_sport), "Deporte", R.drawable.sport))
        categories.add(Category(requireContext().getString(R.string.category_childish), "Infantil", R.drawable.childish))
        categories.add(Category(requireContext().getString(R.string.category_leisure), "Ocio", R.drawable.leisure))
        categories.add(Category(requireContext().getString(R.string.category_theater), "Teatro", R.drawable.theater))
        categories.add(Category(requireContext().getString(R.string.category_literature), "Literatura", R.drawable.literature))
        //categories.add(Category("Visita Guiada", "Visita Guiada", R.drawable.prueba))

    }





}