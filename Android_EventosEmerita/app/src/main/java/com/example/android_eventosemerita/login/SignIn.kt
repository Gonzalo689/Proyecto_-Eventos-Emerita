package com.example.android_eventosemerita.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.databinding.ActivitySignInBinding
import com.example.android_eventosemerita.utils.UtilsFun.remenberUser
import com.example.android_eventosemerita.utils.UtilsFun.validateEmail

/**
 * Actividad para el inicio de sesión.
 */
class SignIn : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var userAPIClient:UserAPIClient
    private var userRoot: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userAPIClient = UserAPIClient(applicationContext)

        binding.startLoggGuest.setOnClickListener{
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

    /**
     * Inicia la actividad principal.
     */
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Inicia la actividad de registro.
     */
    private fun startSignUp() {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Valida los campos de entrada de correo electrónico y contraseña.
     *
     * @return true si los campos son válidos, false de lo contrario.
     */
    private fun validateText(): Boolean{
        val email = binding.emailLog.text.toString().trim()
        val pass = binding.passwordLog.text.toString().trim()

        if (email.isEmpty()) {
            binding.emailLog.error = getString(R.string.error_empty_email)
            binding.emailLog.requestFocus()
            return false
        }
        if (!validateEmail(email)){
            binding.emailLog.error = getString(R.string.error_invalid_email)
            binding.emailLog.requestFocus()
            return false
        }
        if (pass.isEmpty()) {
            binding.passwordLog.error = getString(R.string.error_empty_password)
            binding.passwordLog.requestFocus()
            return false
        }
        if (pass.length < 3) {
            binding.passwordLog.error = getString(R.string.error_short_password)
            binding.passwordLog.requestFocus()
            return false
        }
        return true
    }

    /**
     * Realiza la autenticación del usuario.
     *
     * @param context El contexto de la aplicación.
     */
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
                binding.emailLog.error = getString(R.string.error_invalid_credentials)
                binding.emailLog.requestFocus()
            }
        })

    }







}