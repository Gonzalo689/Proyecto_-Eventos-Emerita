package com.example.android_eventosemerita.fragments_nav

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.FragmentEventBinding
import com.squareup.picasso.Picasso
import android.location.Geocoder
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.ComentAPIClient
import com.example.android_eventosemerita.api.model.Coment
import com.example.android_eventosemerita.controller.AdapterComents
import com.example.android_eventosemerita.utils.UtilsConst.userRoot
import com.example.android_eventosemerita.utils.UtilsFun.addNotification
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


private const val ARG_EVENT = "event"

class FragmentEvent : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener,GoogleMap.OnMapLongClickListener {

    private var event: Event? = null
    private lateinit var binding: FragmentEventBinding
    private lateinit var mMAp : GoogleMap
    private lateinit var adapterComents: AdapterComents
    private lateinit var userAPIClient: UserAPIClient
    private lateinit var comentAPIClient: ComentAPIClient
    private lateinit var mainActivity : MainActivity

    private var listComents: List<Coment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            event = args.getSerializable(ARG_EVENT) as? Event
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainActivity = requireActivity() as MainActivity
        userAPIClient = UserAPIClient(requireContext())
        comentAPIClient = ComentAPIClient(requireContext())
        binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(event: Event) =
            FragmentEvent().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_EVENT, event)
                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        textEvent()
        buttonLike()
        binding.buttonComent.setOnClickListener{
            createComets()
            binding.textMultiLine.setText("")
        }

        binding.allComents.setOnClickListener{
            adapterComents.updateComents(listComents as ArrayList<Coment>)
        }

        getComets()

    }

    fun createComets(){
        val text = binding.textMultiLine.text.toString()
        if(text.isNotEmpty()){
            val comet = Coment(0,text,"2024-4-19",emptyList(), userRoot!!.id,event!!.eventId)
            comentAPIClient.postComent(comet,object :Callback.MyCallback<Coment>{
                override fun onSuccess(data: Coment) {
                    (listComents as ArrayList).add(0,data)
                    adapterComents.updateOneComent(data)
                    moreEventsVisibility()
                }

                override fun onError(errorMsg: Coment?) {}

            })
        }
    }
    fun getComets(){
        comentAPIClient.getComets(event!!.eventId,object :Callback.MyCallback<List<Coment>>{
            override fun onSuccess(data: List<Coment>) {
                if (data.isNotEmpty()){
                    binding.noComents.visibility = View.GONE
                    listComents = data.reversed()
                    moreEventsVisibility()
                    recyclerComents(data.subList(0, 5).reversed() as ArrayList<Coment>)
                }else{
                    recyclerComents(ArrayList())
                }
            }

            override fun onError(errorMsg: List<Coment>?) {
                recyclerComents(ArrayList())
            }

        })
    }
    fun moreEventsVisibility(){
        if (listComents.size > 5){
            binding.allComents.visibility = View.VISIBLE
        }else{
            binding.allComents.visibility = View.GONE
        }
    }

    fun recyclerComents(comentsList: ArrayList<Coment>){
        if (!isAdded) {
            return
        }
        adapterComents = AdapterComents(comentsList, userAPIClient)
        binding.recyclerComents.adapter = adapterComents
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerComents.layoutManager = layoutManager

    }



    fun buttonLike(){
        var isAdd = false
        userAPIClient.isLikedEvent(userRoot!!.id, event!!.eventId,object : Callback.MyCallback<Boolean> {
            override fun onSuccess(data: Boolean){
                checkfollow(data)
                isAdd = data
            }

            override fun onError(errorMsg: Boolean?) {
            }
        })
        binding.buttonLike.setOnClickListener{
            userAPIClient.updateUserList(userRoot!!.id, event!!.eventId,!isAdd,object : Callback.MyCallback<Boolean> {
                override fun onSuccess(data: Boolean) {
                    userRoot?.eventsLikeList!!.add(event!!.eventId)
                    isAdd = data
                    checkfollow(isAdd)

                    addNotification(isAdd,event!!,requireContext())
                }

                override fun onError(errorMsg: Boolean?) {
                }
            })

        }

    }
    fun textEvent(){
        binding.title.text = event?.titulo

        Picasso.get().load(event?.imagenIni).into(binding.imageEvent)

        var descriptionC = ""
        event!!.descriptionCompleta.forEach { des ->
            if (!des.trim().isEmpty()){
                descriptionC += des.trim() + "\n"
            }
        }
        binding.textDescription.text = descriptionC

        binding.textDate.text = "Fecha inicial: " + event!!.stringFecha(event!!.fecha_inicio)

        val finalDate = event?.fecha_final

        if (finalDate != null) {
            if (!finalDate.isEmpty()){
                binding.textDate2.text = "Fecha final: " + event!!.stringFecha(finalDate)
            }else{
                binding.textDate2.visibility = View.GONE
            }
        }
    }

    fun checkfollow(like:Boolean){
        if (like){
            binding.buttonLike.setBackgroundColor(Color.RED)
        }else{
            binding.buttonLike.setBackgroundColor(Color.GREEN)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as? MainActivity)?.setBottomNavVisibility(false)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMAp = p0
        mMAp.setOnMapClickListener(this)
        mMAp.setOnMapLongClickListener(this)
        var location =  LatLng(38.9179933, -6.3429062) // Localización de Mérida centro

        val googleMap = event!!.utlGooglemaps
        val addressEncoded: String?
        if (googleMap.isNotEmpty()) {
            addressEncoded = googleMap.split("&q=")[1]
            val geocoder = Geocoder(requireContext())
            val locationList = geocoder.getFromLocationName(addressEncoded, 1)

            if (locationList?.isNotEmpty() == true) {
                val latitude = locationList[0].latitude
                val longitude = locationList[0].longitude
                location = LatLng(latitude, longitude)
            }
        }
        mMAp.addMarker(MarkerOptions().position(location).title(event!!.titulo))
        mMAp.moveCamera(CameraUpdateFactory.newLatLng(location))


    }

    override fun onMapClick(p0: LatLng) {

    }

    override fun onMapLongClick(p0: LatLng) {

    }

}



