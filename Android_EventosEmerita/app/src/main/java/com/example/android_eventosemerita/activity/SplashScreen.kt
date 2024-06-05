package com.example.android_eventosemerita.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.EventAPIClient
import com.example.android_eventosemerita.databinding.ActivitySplashScreenBinding


/**
 * Actividad SplashScreen que muestra la pantalla de bienvenida mientras se realiza la inicialización de la aplicación.
 */
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var eventAPIClient: EventAPIClient

    /**
     * Método que se ejecuta cuando se crea la actividad.
     * @param savedInstanceState Estado guardado de la actividad, si lo hay.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageView2.setImageResource(R.drawable.event_merida)

        eventAPIClient = EventAPIClient(this)
        makeApiCall()

    }
    /**
     * Método para realizar la llamada a la API y ver si se conecta adecuadamente al servidor si no,
     * volvera a cargar la función
     */
    private fun makeApiCall() {
        val callback = object : Callback.MyCallback<String> {
            override fun onSuccess(data: String) {
                if (data.isNotEmpty() && data == "conect") {
                    navigateToMain()
                }else{
                    Handler(mainLooper).postDelayed({ makeApiCall()}, 2000)
                }
            }

            override fun onError(errorMsg: String?) {
                Handler(mainLooper).postDelayed({ makeApiCall() }, 2000)
            }
        }
        eventAPIClient.getConexion(callback)
    }
    /**
     * Método para navegar a la actividad principal una vez que se ha establecido la conexión.
     */
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}