package com.example.android_eventosemerita.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.databinding.ActivitySignUpBinding
import com.example.android_eventosemerita.utils.UtilsFun.remenberUser
import com.example.android_eventosemerita.utils.UtilsFun.validateEmail

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var userAPIClient: UserAPIClient
    private var userRoot: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userAPIClient = UserAPIClient(applicationContext)

        binding.buttonRegis.setOnClickListener {
            if (validateText()){
                createUser(this)
            }
        }
        binding.buttonTextLogin.setOnClickListener{
            startSingIn()
        }

    }
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("User", userRoot)
        startActivity(intent)
        finish()
    }
    private fun startSingIn() {
        val intent = Intent(this, SignIn::class.java)
        startActivity(intent)
        finish()
    }


    private fun validateText(): Boolean{
        val name = binding.editTextUsername.text.toString().trim()
        val email = binding.editTextEmailRegister.text.toString().trim()
        val pass = binding.editTextPassword.text.toString().trim()
        val confirmPass = binding.confirmPassword.text.toString().trim()

        if (name.isEmpty() || name.length >= 4) {
            binding.editTextUsername.error = getString(R.string.error_short_name)
            binding.editTextUsername.requestFocus()
            return false
        }
        if (email.isEmpty()) {
            binding.editTextEmailRegister.error = getString(R.string.error_empty_email)
            binding.editTextEmailRegister.requestFocus()
            return false
        }
        if (pass.isEmpty()) {
            binding.editTextPassword.error = getString(R.string.error_empty_password)
            binding.editTextPassword.requestFocus()
            return false
        }
        if (!validateEmail(email)){
            binding.editTextEmailRegister.error = getString(R.string.error_invalid_email)
            binding.editTextEmailRegister.requestFocus()
            return false
        }
        if (pass.length < 3) {
            binding.editTextPassword.error = getString(R.string.password)
            binding.editTextPassword.requestFocus()
            return false
        }
        if (!confirmPass.equals(pass)) {
            binding.confirmPassword.error = getString(R.string.error_password_mismatch)
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
                remenberUser(context,data.id)
                startMainActivity()
            }

            override fun onError(errorMsg: User?) {
                println("Error: $errorMsg")
            }
        }
        userAPIClient.createUser(name,email,pass,callback)
    }




}