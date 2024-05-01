package com.example.android_eventosemerita.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var userAPIClient: UserAPIClient
    private var userRoot: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userAPIClient = UserAPIClient(applicationContext)


        binding.buttonRegis.setOnClickListener(View.OnClickListener {
            if (validateText()){
                createUser(this)
            }
        })

    }
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("User", userRoot)

        startActivity(intent)
        finish()
    }

    private fun validateText(): Boolean{
        val name = binding.editTextUsername.text.toString().trim()
        val email = binding.editTextEmailRegister.text.toString().trim()
        val pass = binding.editTextPassword.text.toString().trim()
        val confirmPass = binding.confirmPassword.text.toString().trim()

        if (name.isEmpty()) {
            binding.editTextUsername.error = "El campo del correo electrónico está vacío"
            binding.editTextUsername.requestFocus()
            return false
        }
        if (email.isEmpty()) {
            binding.editTextEmailRegister.error = "El campo del correo electrónico está vacío"
            binding.editTextEmailRegister.requestFocus()
            return false
        }
        if (pass.isEmpty()) {
            binding.editTextPassword.error = "El campo de contraseña está vacío"
            binding.editTextPassword.requestFocus()
            return false
        }
        if (!SignIn.validateEmail(email)){
            binding.editTextEmailRegister.error = "No es un correo válido"
            binding.editTextEmailRegister.requestFocus()
            return false
        }
        if (pass.length < 3) {
            binding.editTextPassword.error = "La constraseña no puede ser tan corta"
            binding.editTextPassword.requestFocus()
            return false
        }
        if (!confirmPass.equals(pass)) {
            binding.confirmPassword.error = "Los campos no coinciden"
            binding.confirmPassword.requestFocus()
            return false
        }
        return true
    }

    private fun createUser(context: Context){
        val name = binding.editTextUsername.text.toString().trim()
        val email = binding.editTextEmailRegister.text.toString().trim()
        val pass = binding.editTextPassword.text.toString().trim()
        val callback = object : Callback.MyCallback<User> {
            override fun onSuccess(data: User) {
                userRoot = data
                SignIn.remenberUser(context,data.id)
                startMainActivity()
            }

            override fun onError(errorMsg: User?) {
                println("Error: $errorMsg")
            }
        }
        userAPIClient.createUser(name,email,pass,callback)
    }




}