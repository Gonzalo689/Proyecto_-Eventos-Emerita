package com.example.android_eventosemerita.fragments_nav

import android.content.res.ColorStateList
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
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.ComentAPIClient
import com.example.android_eventosemerita.api.model.Coment
import com.example.android_eventosemerita.controller.AdapterComents
import com.example.android_eventosemerita.utils.UtilsConst.userRoot
import com.example.android_eventosemerita.utils.UtilsFun.addNotification
import com.example.android_eventosemerita.utils.UtilsFun.dpToPx
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
        if(userRoot!=null){
            buttonLike()
        }else{
            buttonLikeNoRegister()
        }

        inic()

        buttonBack()
        getComets()
        hideIfKeyboardOut()
    }
    fun inic(){
        binding.buttonComent.setOnClickListener{
            if(userRoot==null){
                Toast.makeText(context, "Registrate para poder comentar", Toast.LENGTH_SHORT).show()
            }else{
                createComets()
                binding.textMultiLine.setText("")
            }
        }

        binding.allComents.setOnClickListener{
            adapterComents.updateComents(listComents as ArrayList<Coment>)
            binding.allComents.visibility = View.GONE
        }
        binding.categories.text = event!!.categoria
    }
    fun hideIfKeyboardOut() {
        if (!isAdded) return // Verificar si el fragmento está adjunto a una actividad

        val heightDiffThreshold = dpToPx(requireContext())
        val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (isAdded) {
                val heightDiff: Int = binding.root.rootView.height - binding.root.height
                val margin = resources.getDimensionPixelSize(R.dimen.margin)
                val noMargin = resources.getDimensionPixelSize(R.dimen.no_margin)
                val layoutParams = binding.nestedScrollView.layoutParams as ViewGroup.MarginLayoutParams
                if (heightDiff > heightDiffThreshold) {
                    layoutParams.bottomMargin = noMargin
                    binding.buttonLike.visibility = View.GONE
                } else {
                    layoutParams.bottomMargin = margin
                    binding.buttonLike.visibility = View.VISIBLE
                }
            }
        }

        // Agregar el listener a la vista raíz del fragmento
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)

        // Crear un observer para el ciclo de vida
        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
            }
        }

        viewLifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }




    fun buttonBack(){
        binding.back.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    fun createComets(){
        val text = binding.textMultiLine.text.toString()
        if(text.isNotEmpty()){
            val coment = Coment(0,text,"",emptyList(), userRoot!!.id,event!!.eventId)
            comentAPIClient.postComent(coment,object :Callback.MyCallback<Coment>{
                override fun onSuccess(data: Coment) {
                    binding.noComents.visibility = View.GONE
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
                if (data.isNotEmpty()) {
                    binding.noComents.visibility = View.GONE
                    listComents = data.reversed()
                    moreEventsVisibility()
                    val subListSize = minOf(listComents.size, 5)
                    val dataList = ArrayList(listComents.subList(0,subListSize))
                    recyclerComents(dataList)
                } else {
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
        adapterComents = AdapterComents(comentsList,binding.noComents)
        binding.recyclerComents.adapter = adapterComents
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerComents.layoutManager = layoutManager

    }


    fun buttonLikeNoRegister(){
        binding.buttonLike.setOnClickListener{
            Toast.makeText(context, "Registrate para poder seguir el evento", Toast.LENGTH_SHORT).show()
        }
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

        val dateStart =requireContext().getString(R.string.initial_date_param, event!!.stringFecha(event!!.fecha_inicio))
        binding.textDate.text = dateStart

        val finalDate = event?.fecha_final

        if (finalDate != null) {
            if (!finalDate.isEmpty()){
                val dateFinal =requireContext().getString(R.string.initial_date_param, event!!.stringFecha(finalDate))
                binding.textDate2.text =dateFinal
            }else{
                binding.textDate2.visibility = View.GONE
            }
        }
    }

    fun checkfollow(like:Boolean){
        val red = requireContext().getColor(R.color.red_Primary)
        val white = requireContext().getColor(R.color.white)
        if (like){
            binding.buttonLike.backgroundTintList = ColorStateList.valueOf(white)
            binding.textLike.setTextColor(red)
            binding.textLike.text = requireContext().getText(R.string.following_event)
        }else{
            binding.buttonLike.backgroundTintList = ColorStateList.valueOf(red)
            binding.textLike.setTextColor(white)

            binding.textLike.text = requireContext().getText(R.string.follow_event)
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



