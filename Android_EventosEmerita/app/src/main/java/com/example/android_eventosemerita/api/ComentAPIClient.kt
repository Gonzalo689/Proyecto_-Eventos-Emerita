package com.example.android_eventosemerita.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.android_eventosemerita.api.model.Coment
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.utils.UtilsFun.comentToJson
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class ComentAPIClient  (private val context: Context) {
    //private val url: String = "https://x2t55z6x-3000.uks1.devtunnels.ms"
    private val url: String = "http://10.0.2.2:3000"

    fun postComent(coment: Coment, callback: Callback.MyCallback<Coment>) {
        val url = "$url/comentarios/"
        val queue = Volley.newRequestQueue(context)
        val jsonObject = comentToJson(coment)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                val comentGson = Gson().fromJson(response.toString(), Coment::class.java)
                callback.onSuccess(comentGson)

            },
            { _ ->
                callback.onError(null)
            }
        )

        queue.add(jsonObjectRequest)
    }
    fun getComets(id:Int, callback: Callback.MyCallback<List<Coment>>) {
        val url = "$url/comentarios/$id"
        val queue = Volley.newRequestQueue(context)

        val jsonObjectRequest = StringRequest(Request.Method.GET, url,
            { response ->
                try {

                    val eventList = Gson().fromJson(response, Array<Coment>::class.java).toList()
                    println("A")
                    callback.onSuccess(eventList)
                } catch (e: JsonSyntaxException) {
                    println("B")
                    callback.onError(null)
                }
            },
            { error ->
                println("C")
                callback.onError(null)
            })

        queue.add(jsonObjectRequest)
    }

}