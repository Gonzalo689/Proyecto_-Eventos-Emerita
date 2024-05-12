package com.example.android_eventosemerita.fragments_nav

import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.fragments_nav.FragmentEvent
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.activity.MainActivity.Companion.userRoot
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.databinding.FragmentProfileBinding
import com.example.android_eventosemerita.login.SignIn
import com.example.android_eventosemerita.utils.ImageCircle
import com.example.android_eventosemerita.utils.ImageCircle.Companion.lowerQuality
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class Profile : Fragment() {
    companion object{
        const val NOTIF = "notification"
    }
    private lateinit var binding: FragmentProfileBinding
    private lateinit var uri : Uri
    private lateinit var userAPIClient: UserAPIClient

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            this.uri = it
            Picasso.get()
                .load(it)
                .transform(ImageCircle())
                .into(binding.profileimage)

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val mainActivity = context as MainActivity
        userAPIClient = UserAPIClient(mainActivity)
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logOut.setOnClickListener(View.OnClickListener {
            returnToSplash()
            forgotUser()
        })
        binding.galery.setOnClickListener(View.OnClickListener {
            openGalery()
        })
        image()

        binding.save.setOnClickListener(View.OnClickListener {
            updateImage()
        })
        binding.switchNotf.setOnClickListener {
            val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
            val editor = preferences.edit()
            if (binding.switchNotf.isChecked){
                editor.putBoolean(NOTIF, true)
            }else{
                editor.putBoolean(NOTIF, false)
            }
            editor.apply()
            getEventFavs()
        }

        remenberNotf()



    }

    fun remenberNotf(){
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        binding.switchNotf.isChecked = preferences.getBoolean(NOTIF,true)
    }
    fun getEventFavs(){
        userAPIClient.getFavEventsList(userRoot!!.id, object : Callback.MyCallback<List<Event>> {
            override fun onSuccess(data: List<Event>){
                if (data.isNotEmpty()){
                    for (event in data){
                        FragmentEvent.addNotification(true, event,requireContext())
                    }
                }
            }

            override fun onError(errorMsg: List<Event>?) {
            }
        })
    }

    fun updateImage(){
        CoroutineScope(Dispatchers.IO).launch {
            val byteArray = convertToBytes(uri)
            val imageString = lowerQuality(byteArray)
            println(""+userRoot?.id )
            userRoot?.profilePicture=imageString

            userAPIClient.updateProfilePicture(userRoot!!.id, imageString, object : Callback.MyCallback<String> {
                override fun onSuccess(data: String) {
                    activity?.runOnUiThread {
                        image()
                        println("bien")
                    }
                }

                override fun onError(errorMsg: String?) {

                    activity?.runOnUiThread {
                        println("Mal")
                    }
                }
            })
        }
    }

    fun decodeBase64ToFile(base64Image: String) :File{
        val file = File(requireContext().cacheDir, "image.jpg")
        if (file.exists()) {
            file.delete()
        }
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        file.writeBytes(decodedBytes)
        return file
    }
    fun image(){
        val img = userRoot?.profilePicture
        if (img!!.isNotEmpty()){
            val file= decodeBase64ToFile(img)
            Picasso.get()
                .load(file)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .transform(ImageCircle())
                .into(binding.profileimage)
        }else{
            Picasso.get()
                .load(R.drawable.prueba)
                .transform(ImageCircle())
                .into(binding.profileimage)
        }
    }


    fun forgotUser(){
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = preferences.edit()
        editor.putBoolean(SignIn.REMEMBER, false)
        editor.putInt(SignIn.USER_ID, 0)
        editor.apply()
    }
    fun returnToSplash(){
        val intent = Intent(requireContext(), SignIn::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
    private fun convertToBytes(path: Uri): ByteArray? {
        val format = if (Build.VERSION.SDK_INT >= 30) CompressFormat.WEBP_LOSSLESS else CompressFormat.WEBP
        val outputStream = ByteArrayOutputStream()
        return try {
            val inputStream: InputStream? = requireContext().contentResolver?.openInputStream(path)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            bitmap.compress(format, 100, outputStream)
            return outputStream.toByteArray()
        } catch (e: Exception) {
            null

        }
    }
    private fun openGalery(){
        galleryLauncher.launch("image/*")
    }
}

