package com.example.android_eventosemerita.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.android_eventosemerita.api.model.Event
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class EventAPIClient (private val context: Context) {

    //private val url: String = "https://x2t55z6x-3000.uks1.devtunnels.ms"
    private val url: String = "http://10.0.2.2:3000"

    fun getConexion(callback: Callback.MyCallback<String>) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                callback.onSuccess(response)
            },
            { error ->
                val errorString = error.toString()
                callback.onError(errorString)
            })


        queue.add(stringRequest)
    }
    fun getEventsDest(callback: Callback.MyCallback<List<Event>>) {
        val queue = Volley.newRequestQueue(context)

        val url = "$url/eventos/destacados"

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val responseObject = Gson().fromJson(response, Array<Event>::class.java).toList()
                callback.onSuccess(responseObject)
            },
            { error ->
                callback.onError(null)
            })


        queue.add(stringRequest)
    }
    fun getEventsDestPast(callback: Callback.MyCallback<List<Event>>) {
        val queue = Volley.newRequestQueue(context)

        val url = "$url/eventos/past"

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val responseObject = Gson().fromJson(response, Array<Event>::class.java).toList()
                callback.onSuccess(responseObject)
            },
            { error ->
                callback.onError(null)
            })
        queue.add(stringRequest)
    }
    fun getWeekend(callback: Callback.MyCallback<List<Event>>) {
        val queue = Volley.newRequestQueue(context)

        val url = "$url/eventos/weekend"

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val responseObject = Gson().fromJson(response, Array<Event>::class.java).toList()
                callback.onSuccess(responseObject)
            },
            { error ->
                callback.onError(null)
            })
        queue.add(stringRequest)
    }
    fun getEventCategory(category:String, callback: Callback.MyCallback<List<Event>>) {
        val queue = Volley.newRequestQueue(context)

        val url = "$url/eventos/$category"

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val responseObject = Gson().fromJson(response, Array<Event>::class.java).toList()
                callback.onSuccess(responseObject)
            },
            { error ->
                val errorString = error.toString()
                callback.onError(null)
            })


        queue.add(stringRequest)
    }
    fun getAllEvents(callback: Callback.MyCallback<List<Event>>) {
        val queue = Volley.newRequestQueue(context)

        val url = "$url/eventos/"

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val responseObject = Gson().fromJson(response, Array<Event>::class.java).toList()
                callback.onSuccess(responseObject)
            },
            { error ->
                callback.onError(null)
            })


        queue.add(stringRequest)
    }


}