package com.example.android_eventosemerita.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.android_eventosemerita.api.model.Event
import com.google.gson.Gson
import org.json.JSONObject

class EventAPIClient (private val context: Context) {

    fun getAllEvents(callback: Callback.MyCallback<List<Event>>) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        val url = "http://10.0.2.2:3000/eventos/"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val responseObject = Gson().fromJson(response, Array<Event>::class.java).toList()
                callback.onSuccess(responseObject)
            },
            { error ->
                // Handle errors
                val errorString = error.toString()
                callback.onError(errorString)
            })


        queue.add(stringRequest)
    }

    fun usuarios(id:Int, callback: Callback.MyCallback<String>) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        val url = "http://10.0.2.2:3000/usuarios/$id"
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                // Display the response in the TextView.
                // val responseObject = Gson().fromJson(response, Array<Event>::class.java).toList()
                //callback.onSuccess(resp)
            },
            { error ->
                // Handle errors, e.g., show an error message.
                callback.onError(error.toString())
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
    fun makePostRequest(url: String, params: Map<String, String>) {

        // Convert the params map to a JSONObject
        val jsonParams = JSONObject(params)

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)

        // Request a JSON response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonParams,
            { response ->
                // Handle response
                println("Respuesta: $response")
            },
            { error ->
                // Handle error
                println("Error: ${error.message}")
            })

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }
}