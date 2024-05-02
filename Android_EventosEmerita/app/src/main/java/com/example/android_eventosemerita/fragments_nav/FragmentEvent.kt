package com.example.android_eventosemerita.fragments_nav

import android.annotation.SuppressLint
import android.content.Intent
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
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.activity.MainActivity.Companion.userRoot
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.notify.AlarmNotification
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Calendar

private const val ARG_EVENT = "event"


class FragmentEvent : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener,GoogleMap.OnMapLongClickListener {
    private var event: Event? = null
    private lateinit var binding: FragmentEventBinding
    private lateinit var mMAp : GoogleMap
    private lateinit var userAPIClient: UserAPIClient
    private lateinit var mainActivity : MainActivity

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
    @SuppressLint("QueryPermissionsNeeded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var isAdd = false
        binding.title.text = event?.titulo
        Picasso.get().load(event?.imagenIni).into(binding.imageEvent)

        var descriptionC = ""
        event?.descriptionCompleta?.forEach { des ->
            if (!des.trim().isEmpty()){
                descriptionC += des.trim() + "\n"
            }
        }
        binding.textDescription.text = descriptionC

        binding.textDate.text = "Fecha inicial: " + stringFecha(event!!.fecha_inicio)

        val finalDate = event?.fecha_final

        if (finalDate != null) {
            if (!finalDate.isEmpty()){
                binding.textDate2.text = "Fecha final: " + stringFecha(finalDate)
            }else{
                binding.textDate2.visibility = View.GONE
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as? SupportMapFragment
        mapFragment?.getMapAsync(this)


        val callbackLik = object : Callback.MyCallback<Boolean> {
            override fun onSuccess(data: Boolean){
                println("good " + data)
                checkfollow(data)
                isAdd = data
            }

            override fun onError(errorMsg: Boolean?) {
            }
        }

        userAPIClient.isLikedEvent(userRoot!!.id, event!!.eventId,callbackLik)



        binding.buttonLike.setOnClickListener(View.OnClickListener {
            val callback = object : Callback.MyCallback<Boolean> {
                override fun onSuccess(data: Boolean) {
                    userRoot?.eventsLikeList!!.add(event!!.eventId)
                    isAdd = data
                    checkfollow(isAdd)
                    addNotification(isAdd)
                }

                override fun onError(errorMsg: Boolean?) {
                }
            }

            userAPIClient.updateUserList(userRoot!!.id, event!!.eventId,!isAdd,callback)

        })


    }
    fun addNotification(isAdd:Boolean){
        val dateSplit = event!!.fecha_inicio.split(",")[0]

        val calendar = Calendar.getInstance()
        val yearNow = calendar.get(Calendar.YEAR)
        val monthNow = calendar.get(Calendar.MONTH) + 1
        val dayNow = calendar.get(Calendar.DAY_OF_MONTH)
        val dateNow = "$yearNow-$monthNow-$dayNow"
        val dateevent = "$dateSplit"
        println(dateNow)
        println(dateevent)
//        if (isAdd){
//            mainActivity.sheduleNotification(event!!)
//        }else{
//            mainActivity.cancelNotification(requireContext(),event!!.eventId)
//        }
    }
    fun checkfollow(like:Boolean){
        if (like){
            binding.buttonLike.setBackgroundColor(Color.RED)
        }else{
            binding.buttonLike.setBackgroundColor(Color.GREEN)
        }

    }
    fun stringFecha(date:String):String{
        val dateSplit = date.split(",")[0].split("-")
        return dateSplit[2] + " de " + nameMonth(dateSplit[1]) + " del " + dateSplit[0]
    }
    fun nameMonth(number: String) : String {
        val monthName = when (number) {
            "01" -> "Enero"
            "02" -> "Febrero"
            "03" -> "Marzo"
            "04" -> "Abril"
            "05" -> "Mayo"
            "06" -> "Junio"
            "07" -> "Julio"
            "08" -> "Agosto"
            "09" -> "Septiembre"
            "10" -> "Octubre"
            "11" -> "Noviembre"
            "12" -> "Diciembre"
            else -> error("Fallo al recoger el mes")
        }
        return monthName
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as? MainActivity)?.setBottomNavVisibility(false)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMAp = p0
        mMAp.setOnMapClickListener(this)
        mMAp.setOnMapLongClickListener(this)
        var location =  LatLng(38.9179933, -6.3429062)

        val googleMap = event?.utlGooglemaps

        val addressEncoded: String?
        if (googleMap!=null) {
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


