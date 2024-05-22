package com.example.android_eventosemerita.fragments_nav

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.widget.ImageView
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
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.activity.SplashScreen
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.databinding.FragmentProfileBinding
import com.example.android_eventosemerita.login.SignIn
import com.example.android_eventosemerita.utils.Image
import com.example.android_eventosemerita.utils.Image.Companion.decodeBase64ToFile
import com.example.android_eventosemerita.utils.Image.Companion.imgFile
import com.example.android_eventosemerita.utils.Image.Companion.imgPred
import com.example.android_eventosemerita.utils.UtilsConst.NOTIF
import com.example.android_eventosemerita.utils.UtilsConst.REMEMBER
import com.example.android_eventosemerita.utils.UtilsConst.USER_ID
import com.example.android_eventosemerita.utils.UtilsConst.userRoot
import com.example.android_eventosemerita.utils.UtilsFun
import com.example.android_eventosemerita.utils.UtilsFun.addNotification
import com.example.android_eventosemerita.utils.UtilsFun.lowerQuality
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.coroutines.suspendCoroutine

private const val NAME_FILE = "image"

class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var uri : Uri
    private lateinit var userAPIClient: UserAPIClient
    private var deployEdit = false
    private var galleryOpened = false


    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            this.uri = it
            galleryOpened = true

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        userAPIClient = UserAPIClient(requireContext())
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logOut.setOnClickListener(View.OnClickListener {
            forgotUser()
            returnToSplash()

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

        binding.edit.setOnClickListener {
            if (deployEdit){
                changeIconDeploy(R.drawable.ic_arrow_up)
                deployEdit = false
                binding.editProfile.visibility = View.GONE
            }else{
                changeIconDeploy(R.drawable.ic_arrow_down)
                deployEdit = true
                binding.editProfile.visibility = View.VISIBLE
            }
        }
        binding.confirmButton.setOnClickListener{
            CoroutineScope(Dispatchers.Main).launch {
                updateUser()
            }
        }

        remenberNotf()
        editProfie()
    }
    override fun onResume() {
        super.onResume()
        if (galleryOpened) {
            CoroutineScope(Dispatchers.Main).launch {
                openAlertDialogAgain()
            }
        }
    }
    private fun editProfie(){
        var file: File? = null
        val imgView = binding.profileimage
        if (userRoot!!.profilePicture.isNotEmpty()){
            file = decodeBase64ToFile(userRoot!!.profilePicture,requireContext(),NAME_FILE)
            imgFile(file,imgView)
        }else{
            imgPred(imgView)
        }
        binding.profileimage.setOnClickListener{
            showFullImageFromCache(file,userRoot!!.profilePicture)
        }
        //image(userRoot!!.profilePicture, binding.profileimage, requireContext())
        binding.editEmail.hint = userRoot!!.email
        binding.editName.hint = userRoot!!.nombre

    }
//    fun imgFile(file: File, imageView: ImageView){
//        Picasso.get()
//            .load(file)
//            .transform(Image())
//            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//            .into(imageView)
//    }
//    fun imgPred(imageView: ImageView){
//        Picasso.get()
//            .load(R.drawable.prueba)
//            .transform(Image())
//            .into(imageView)
//    }
    fun showFullImageFromCache(file: File?, imageString: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val imageView = ImageView(requireContext())
        imageView.adjustViewBounds = true

        if (file != null){
            imgFile(file,imageView)
        }else{
            imgPred(binding.profileimage)
        }

        dialogBuilder.setView(imageView)

        dialogBuilder.setPositiveButton("Galery") { dialog, which ->
            openGalery()
            dialog.dismiss()
        }
        dialogBuilder.setNeutralButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Guardar") { dialog, which ->
            updateImage(imageString)
            dialog.dismiss()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

    }


    fun changeIconDeploy(ic:Int){
        val newDrawable = ContextCompat.getDrawable(requireContext(), ic)
        binding.editTextArrow.setCompoundDrawablesWithIntrinsicBounds(null,null,newDrawable,null)

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
                        addNotification(true, event,requireContext())
                    }
                }
            }

            override fun onError(errorMsg: List<Event>?) {
            }
        })
    }

    fun updateImage(imageString: String){
        binding.progressBar.visibility = View.VISIBLE
        userAPIClient.updateProfilePicture(userRoot!!.id, imageString, object : Callback.MyCallback<String> {
        override fun onSuccess(data: String) {
            activity?.runOnUiThread {
                userRoot!!.profilePicture = imageString
                val file = decodeBase64ToFile(imageString,requireContext(),NAME_FILE)
                binding.progressBar.visibility = View.GONE
                imgFile(file,binding.profileimage)
            }
        }

        override fun onError(errorMsg: String?) {
            activity?.runOnUiThread {
                binding.progressBar.visibility = View.GONE
            }
        }
    })
    }

//    fun image(img:String, imageView: ImageView , context: Context): File? {
//        var file :File? = null
//        if (img.isNotEmpty()){
//            file = decodeBase64ToFile(img,context)
//            Picasso.get()
//                .load(file)
//                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                .transform(Image())
//                .into(imageView)
//        }else{
//            Picasso.get()
//                .load(R.drawable.prueba)
//                .transform(Image())
//                .into(imageView)
//        }
//        return file
//    }

    fun forgotUser(){
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = preferences.edit()
        editor.putBoolean(REMEMBER, false)
        editor.putInt(USER_ID, 0)
        editor.apply()
    }
    fun returnToSplash(){
        val intent = Intent(requireContext(), SplashScreen::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun openGalery(){
        galleryLauncher.launch("image/*")
        binding.progressBar.visibility = View.VISIBLE
    }

    fun openAlertDialogAgain(){
        CoroutineScope(Dispatchers.Main).launch {
            val byteArray = Image.convertToBytes(uri,requireContext())
            val imageString = lowerQuality(byteArray)
            val file = decodeBase64ToFile(imageString,requireContext(),NAME_FILE)
            imgFile(file,binding.profileimage)
            //val file = image(imageString,binding.profileimage, requireContext())
            showFullImageFromCache(file,imageString)
            binding.progressBar.visibility = View.GONE
            galleryOpened = false
        }
    }

    private suspend fun updateUser(){
        var name = binding.editName.text.toString().trim()
        var email = binding.editEmail.text.toString().trim()
        val pass = binding.editPass.text.toString().trim()
        val confirmPass = binding.editConfirm.text.toString().trim()

        if (name.isEmpty() || name.length < 4) {
            name = userRoot!!.nombre
        }
        if (email.isEmpty()) {
            email = userRoot!!.email
        }else{
            val isUsedEmail = isEmailUsed(email)
            if (!UtilsFun.validateEmail(email)){
                binding.editEmail.error = "No es un correo válido"
                binding.editEmail.requestFocus()
                return
            }
            if (isUsedEmail) {
                binding.editEmail.error = "Ya existe alguien con ese usuario"
                binding.editEmail.requestFocus()
                return
            }
        }
        if (pass.length in 1..3) {
            binding.editPass.error = "Contraseña demasiada corta"
            binding.editPass.requestFocus()
            return
        }
        if (confirmPass != pass) {
            binding.editConfirm.error = "Los campos no coinciden"
            binding.editConfirm.requestFocus()
            return
        }

        updateUserBD(name,email,pass)
        return
    }

    private suspend fun isEmailUsed(email: String): Boolean {
        return suspendCoroutine { continuation ->
            val callback = object : Callback.MyCallback<Boolean> {
                override fun onSuccess(data: Boolean) {
                    continuation.resume(data)
                }

                override fun onError(errorMsg: Boolean?) {
                    continuation.resume(true)
                }
            }
            userAPIClient.isEmailUsed(email, callback)
        }
    }

    private fun updateUserBD(name:String, email:String, pass:String){
        userAPIClient.updateUser(userRoot!!.id,name,email,pass, object : Callback.MyCallback<User>{
            override fun onSuccess(data: User) {
                resetEdit(name,email)
                Toast.makeText(context, "Usuario Actualizado", Toast.LENGTH_SHORT).show()
            }

            override fun onError(errorMsg: User?) {
                Toast.makeText(context, "No se pudo actualizar el usuario", Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun resetEdit(name:String, email: String){
        userRoot!!.email = email
        userRoot!!.nombre = name
        binding.editEmail.hint = email
        binding.editName.hint = name
        binding.editName.setText("")
        binding.editEmail.setText("")
        binding.editPass.setText("")
        binding.editConfirm.setText("")
    }


}

