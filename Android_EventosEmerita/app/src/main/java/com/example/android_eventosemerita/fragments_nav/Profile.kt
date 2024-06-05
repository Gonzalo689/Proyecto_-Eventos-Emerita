package com.example.android_eventosemerita.fragments_nav

import android.app.AlertDialog
import android.widget.ImageView
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.EventAPIClient
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
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.suspendCoroutine

private const val NAME_FILE = "image"

/**
 * Fragment representing the user profile.
 */
class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var uri : Uri
    private lateinit var userAPIClient: UserAPIClient
    private var deployEdit = false
    private var galleryOpened = false

    /** Lanzador de resultados de actividad para la galería */
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            this.uri = it
            galleryOpened = true

        }
    }

    /**
     * Se llama para que el fragmento instancie su vista de interfaz de usuario.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        userAPIClient = UserAPIClient(requireContext())
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    /**
     * Se llama inmediatamente después de que onCreateView() haya devuelto, pero antes de que se
     *  haya restaurado algún estado guardado en la vista.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (userRoot != null){
            buttons()
            remenberNotf()
            editProfie()
        }else{
            startNoUser()
        }

    }

    /**
     * Se llama cuando el fragmento es visible para el usuario y se está ejecutando activamente para
     * activar el dialogo
     */
    override fun onResume() {
        super.onResume()
        if (galleryOpened) {
            CoroutineScope(Dispatchers.Main).launch {
                openAlertDialogAgain()
            }
        }
    }
    /**
     * Oculta la vista del perfil y muestra un mensaje cuando no hay usuario registrado.
     */
    fun startNoUser(){
        //binding.frameProfile.visibility = View.GONE
        imgPred(binding.profileimage)
        binding.layautNoUser.visibility = View.VISIBLE
        binding.buttonLog.setOnClickListener{
            val intent = Intent(requireContext(), SignIn::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    /**
     * Configura varios listeners para los botones.
     */
    private fun buttons(){
        binding.logOut.setOnClickListener{
            forgotUser()
            returnToSplash()
        }
        binding.switchNotf.setOnClickListener {
            val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
            val editor = preferences.edit()
            if (binding.switchNotf.isChecked){
                editor.putBoolean(NOTIF, true)
                binding.iconNotif.setImageResource(R.drawable.ic_notifications_active)
            }else{
                editor.putBoolean(NOTIF, false)
                binding.iconNotif.setImageResource(R.drawable.ic_notifications)
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
    }

    /**
     * Actualiza la vista del perfil del usuario con los datos actuales del usuario.
     */
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
        binding.editEmail.hint = userRoot!!.email
        binding.editName.hint = userRoot!!.nombre
        binding.progressBar.visibility = View.GONE
        binding.darkOverlay.visibility = View.GONE
        galleryOpened = false

    }

    /**
     * Muestra la imagen de perfil en tamaño completo desde la caché.
     *
     * @param file Archivo de imagen de perfil
     * @param imageString Cadena de imagen de perfil codificada en base64
     */
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

        dialogBuilder.setPositiveButton(requireContext().getString(R.string.galery)) { dialog, which ->
            openGalery()
            dialog.dismiss()
        }
        dialogBuilder.setNeutralButton(requireContext().getString(R.string.cancel)) { dialog, which ->
            editProfie()
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton(requireContext().getString(R.string.save)) { dialog, which ->
            updateImage(imageString)
            dialog.dismiss()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

    }

    /**
     * Cambia el icono de despliegue en la interfaz de usuario.
     *
     * @param ic ID del recurso del icono a mostrar
     */
    fun changeIconDeploy(ic:Int){
        val newDrawable = ContextCompat.getDrawable(requireContext(), ic)
        binding.editTextArrow.setCompoundDrawablesWithIntrinsicBounds(null,null,newDrawable,null)

    }
    /**
     * Recuerda la configuración de notificaciones del usuario.
     */
    fun remenberNotf(){
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        binding.switchNotf.isChecked = preferences.getBoolean(NOTIF,true)
    }

    /**
     * Obtiene la lista de eventos favoritos del usuario y agrega notificaciones para cada evento.
     */
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

    /**
     * Actualiza la imagen de perfil del usuario en el servidor y en la vista.
     *
     * @param imageString Cadena que representa la nueva imagen de perfil codificada en base64
     */
    fun updateImage(imageString: String){
        binding.progressBar.visibility = View.VISIBLE
        binding.darkOverlay.visibility = View.VISIBLE
        val img = userRoot!!.profilePicture
        userRoot!!.profilePicture = imageString
        userAPIClient.updateProfilePicture(userRoot!!.id, imageString, object : Callback.MyCallback<String> {
            override fun onSuccess(data: String) {
                activity?.runOnUiThread {
                    val file = decodeBase64ToFile(imageString,requireContext(),NAME_FILE)
                    binding.progressBar.visibility = View.GONE
                    binding.darkOverlay.visibility = View.GONE
                    imgFile(file,binding.profileimage)
                }
            }

            override fun onError(errorMsg: String?) {
                activity?.runOnUiThread {
                    userRoot!!.profilePicture = img
                    binding.progressBar.visibility = View.GONE
                    binding.darkOverlay.visibility = View.GONE
                }
            }
        })
    }

    /**
     * Olvida al usuario iniciado sesión.
     */
    fun forgotUser(){
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = preferences.edit()
        editor.putBoolean(REMEMBER, false)
        editor.putInt(USER_ID, 0)
        editor.apply()
    }

    /**
     * Regresa a la pantalla de inicio de sesión.
     */
    fun returnToSplash(){
        val intent = Intent(requireContext(), SignIn::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    /**
     * Abre la galería para seleccionar una imagen.
     */
    private fun openGalery(){
        galleryLauncher.launch("image/*")
        binding.progressBar.visibility = View.VISIBLE
        binding.darkOverlay.visibility = View.VISIBLE
    }

    /**
     * Muestra nuevamente el diálogo de alerta para seleccionar una imagen después de regresar de la galería.
     */
    fun openAlertDialogAgain() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                binding.darkOverlay.visibility = View.VISIBLE

                withContext(Dispatchers.IO) {
                    val byteArray = Image.convertToBytes(uri, requireContext())
                    val imageString = lowerQuality(byteArray)
                    val file = decodeBase64ToFile(imageString, requireContext(), NAME_FILE)

                    withContext(Dispatchers.Main) {
                        imgFile(file, binding.profileimage)
                        showFullImageFromCache(file, imageString)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.darkOverlay.visibility = View.GONE
                galleryOpened = false
            }
        }
    }

    /**
     * Actualiza los datos del usuario en el servidor.
     */
    private suspend fun updateUser(){
        var name = binding.editName.text.toString().trim()
        var email = binding.editEmail.text.toString().trim()
        val pass = binding.editPass.text.toString().trim()
        val confirmPass = binding.editConfirm.text.toString().trim()

        if (name.isEmpty() || name.length <= 4) {
            name = userRoot!!.nombre
        }
        if (email.isEmpty()) {
            email = userRoot!!.email
        }else{
            val isUsedEmail = isEmailUsed(email)
            if (!UtilsFun.validateEmail(email)){
                binding.editEmail.error = requireContext().getString(R.string.error_invalid_email)
                binding.editEmail.requestFocus()
                return
            }
            if (isUsedEmail) {
                binding.editEmail.error = requireContext().getString(R.string.error_email_exists)
                binding.editEmail.requestFocus()
                return
            }
        }
        if (pass.length in 1..3) {
            binding.editPass.error = requireContext().getString(R.string.error_short_password)
            binding.editPass.requestFocus()
            return
        }
        if (confirmPass != pass) {
            binding.editConfirm.error = requireContext().getString(R.string.error_password_mismatch)
            binding.editConfirm.requestFocus()
            return
        }

        updateUserBD(name,email,pass)
        return
    }

    /**
     * Verifica si un correo electrónico ya está en uso.
     *
     * @param email Correo electrónico a verificar
     * @return `true` si el correo electrónico está en uso, `false` de lo contrario
     */
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

    /**
     * Actualiza los datos del usuario en la base de datos.
     *
     * @param name Nombre del usuario
     * @param email Correo electrónico del usuario
     * @param pass Contraseña del usuario
     */
    private fun updateUserBD(name:String, email:String, pass:String){
        userAPIClient.updateUser(userRoot!!.id,name,email,pass, object : Callback.MyCallback<User>{
            override fun onSuccess(data: User) {
                resetEdit(name,email)
                Toast.makeText(context, requireContext().getString(R.string.user_updated), Toast.LENGTH_SHORT).show()
            }

            override fun onError(errorMsg: User?) {
                Toast.makeText(context, requireContext().getString(R.string.user_update_failed), Toast.LENGTH_SHORT).show()
            }

        })
    }

    /**
     * Restablece la interfaz de usuario después de la actualización de los datos del usuario.
     *
     * @param name Nombre actualizado del usuario
     * @param email Correo electrónico actualizado del usuario
     */
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

