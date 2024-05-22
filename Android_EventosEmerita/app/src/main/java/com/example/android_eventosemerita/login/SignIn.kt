package com.example.android_eventosemerita.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.databinding.ActivitySignInBinding
import com.example.android_eventosemerita.utils.UtilsConst.REMEMBER
import com.example.android_eventosemerita.utils.UtilsFun.remenberUser
import com.example.android_eventosemerita.utils.UtilsFun.validateEmail
import java.util.Objects


class SignIn : AppCompatActivity() {

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

        userAPIClient.loginUser(email, pass ,object : Callback.MyCallback<User>{
            override fun onSuccess(data: User) {
                userRoot = data
                remenberUser(context, data.id)
                startMainActivity()
            }
            override fun onError(errorMsg: User?) {
                binding.emailLog.error = "No existe este email con esta contraseña"
                binding.emailLog.requestFocus()
            }
        })

    }







}