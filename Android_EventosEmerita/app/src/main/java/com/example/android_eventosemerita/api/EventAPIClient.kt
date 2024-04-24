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
    private val url: String? = "https://x2t55z6x-3000.uks1.devtunnels.ms/"


    fun getConexion(callback: Callback.MyCallback<String>) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                callback.onSuccess("true")
            },
            { error ->
                val errorString = error.toString()
                callback.onError(errorString)
            })


        queue.add(stringRequest)
    }
    fun getEventsDest(callback: Callback.MyCallback<List<Event>>) {
        val queue = Volley.newRequestQueue(context)

        val url = url + "eventos/destacados"

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
    fun getEventCategory(category:String, callback: Callback.MyCallback<List<Event>>) {
        val queue = Volley.newRequestQueue(context)

        val url = url + "eventos/" + category

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
    fun getAllEvents(callback: Callback.MyCallback<List<Event>>) {
        val queue = Volley.newRequestQueue(context)

        val url = url + "eventos/"

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val responseObject = Gson().fromJson(response, Array<Event>::class.java).toList()
                callback.onSuccess(responseObject)
            },
            { error ->
                val errorString = error.toString()
                callback.onError(errorString)
            })


        queue.add(stringRequest)
    }

}