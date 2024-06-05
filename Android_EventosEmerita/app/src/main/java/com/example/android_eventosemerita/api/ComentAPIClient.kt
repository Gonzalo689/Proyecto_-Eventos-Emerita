package com.example.android_eventosemerita.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.android_eventosemerita.api.model.Coment
import com.example.android_eventosemerita.utils.UtilsFun.comentToJson
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.example.android_eventosemerita.utils.UtilsConst.url


/**
 * Cliente para realizar operaciones relacionadas con comentarios utilizando la API.
 * @property context Contexto de la aplicación.
 */
class ComentAPIClient  (private val context: Context) {
//    private val url: String = "https://x2t55z6x-3000.uks1.devtunnels.ms"
//    private val url: String = "http://10.0.2.2:3000"

    /**
     * Envía un comentario al servidor.
     * @param coment El comentario a enviar.
     * @param callback Callback para manejar el resultado de la operación.
     */
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
    /**
     * Obtiene los comentarios asociados a un evento específico.
     * @param id El ID del evento.
     * @param callback Callback para manejar el resultado de la operación.
     */
    fun getComets(id:Int, callback: Callback.MyCallback<List<Coment>>) {
        val url = "$url/comentarios/$id"
        val queue = Volley.newRequestQueue(context)

        val jsonObjectRequest = StringRequest(Request.Method.GET, url,
            { response ->
                try {

                    val eventList = Gson().fromJson(response, Array<Coment>::class.java).toList()
                    callback.onSuccess(eventList)
                } catch (e: JsonSyntaxException) {
                    callback.onError(null)
                }
            },
            { error ->
                callback.onError(null)
            })

        queue.add(jsonObjectRequest)
    }
    /**
     * Elimina un comentario del servidor.
     * @param id El ID del comentario a eliminar.
     * @param callback Callback para manejar el resultado de la operación.
     */
    fun deleteComent(id: Int, callback: Callback.MyCallback<String>) {
        val url = "$url/comentarios/$id"
        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(Request.Method.DELETE, url,
            { response ->
                try {
                    callback.onSuccess(response)
                } catch (e: JsonSyntaxException) {
                    callback.onError("Error parsing response: ${e.localizedMessage}")
                }
            },
            { error ->
                val errorMsg = error.message ?: "Unknown error"
                callback.onError(errorMsg)
            })

        queue.add(stringRequest)
    }


}