package com.example.android_eventosemerita.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.databinding.ActivitySignInBinding


class SignIn : AppCompatActivity() {
    companion object{
        const val REMEMBER = "remenber"

        fun validateEmail(email: String): Boolean {
            return email.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }
        }

        fun remenberUser(context:Context) {
            val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putBoolean(REMEMBER, true)
            editor.apply()
        }
    }
    private lateinit var binding: ActivitySignInBinding
    private lateinit var userAPIClient:UserAPIClient
    private var userRoot: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userAPIClient = UserAPIClient(applicationContext)

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getBoolean(REMEMBER, false)) {
            startMainActivity()
        }

        binding.buttonLogin.setOnClickListener(View.OnClickListener {
            if (validateText()){
                logUser(this)
            }
        })


        binding.buttonTextRegister.setOnClickListener(View.OnClickListener {
            startSignUp()
        })

    }
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("User", userRoot)
        startActivity(intent)
        finish()
    }
    private fun startSignUp() {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateText(): Boolean{
        val email = binding.emailLog.text.toString().trim()
        val pass = binding.passwordLog.text.toString().trim()

        if (email.isEmpty()) {
            binding.emailLog.error = "El campo del correo electrónico está vacío"
            binding.emailLog.requestFocus()
            return false
        }
        if (!validateEmail(email)){
            binding.emailLog.error = "No es un correo válido"
            binding.emailLog.requestFocus()
            return false
        }
        if (pass.isEmpty()) {
            binding.passwordLog.error = "El campo de contraseña está vacío"
            binding.passwordLog.requestFocus()
            return false
        }
        if (pass.length < 3) {
            binding.passwordLog.error = "La constraseña no puede ser tan corta"
            binding.passwordLog.requestFocus()
            return false
        }
        return true
    }

    private fun logUser(context: Context){
        val email = binding.emailLog.text.toString().trim()
        val pass = binding.passwordLog.text.toString().trim()

        val callback = object : Callback.MyCallback<User> {
            override fun onSuccess(data: User) {
                userRoot = data
                remenberUser(context)
                startMainActivity()
            }
            override fun onError(errorMsg: String) {
                binding.emailLog.error = "No existe este email con esta contraseña"
                binding.emailLog.requestFocus()
            }
        }
        userAPIClient.loginUser(email, pass ,callback)
    }




    private fun getUser(){
        val callback = object : Callback.MyCallback<User> {
            override fun onSuccess(data: User) {
                binding.textEE.text = data.nombre
            }

            override fun onError(errorMsg: String) {
                binding.textEE.text = "todo mal"
                println("Error: $errorMsg")
            }
        }
        userAPIClient.getUserById(1,callback)
    }


    private fun updateUser(){
        val callback = object : Callback.MyCallback<User> {
            override fun onSuccess(data: User) {
                binding.textEE.text = data.nombre
            }

            override fun onError(errorMsg: String) {
                binding.emailLog.error = "No existe este email con esta contraseña"
                binding.emailLog.requestFocus()
            }
        }
        //Cuidado con la contraseña se encripta lo encriptado
        userAPIClient.updateUser(1,"Admin","Admin",callback)
    }




}