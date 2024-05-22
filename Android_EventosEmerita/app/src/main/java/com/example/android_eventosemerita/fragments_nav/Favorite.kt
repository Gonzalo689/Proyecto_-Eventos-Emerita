package com.example.android_eventosemerita.fragments_nav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.controller.Favs.AdapterFavs
import com.example.android_eventosemerita.databinding.FragmentFavoriteBinding
import com.example.android_eventosemerita.utils.UtilsConst.userRoot


class Favorite : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var userAPIClient: UserAPIClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        userAPIClient = UserAPIClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getEventFavs()

    }
    fun getEventFavs(){
        val callback = object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>){
                if (data.isNotEmpty()){
                    val reversedList = ArrayList<Event>(data.size)
                    for (i in data.indices.reversed()) {
                        reversedList.add(data[i])
                    }
                    recyclerFavs(reversedList)
                }else{
                    recyclerFavs(ArrayList())
                }

            }

            override fun onError(errorMsg: List<Event>?) {
                recyclerFavs(ArrayList())
            }
        }

        userAPIClient.getFavEventsList(userRoot!!.id, callback)
    }

    fun recyclerFavs(eventsFavList: ArrayList<Event>){
        if (!isAdded) {
            return
        }
        if (eventsFavList.isNotEmpty()){
            val mainActivity = requireActivity() as MainActivity
            val adapter = AdapterFavs(eventsFavList, mainActivity)
            binding.recyclerFavs.adapter = adapter
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.recyclerFavs.layoutManager = layoutManager
        }else{
            binding.recyclerFavs.visibility = View.GONE
            binding.layautNoEvent.visibility = View.VISIBLE
        }
    }


}