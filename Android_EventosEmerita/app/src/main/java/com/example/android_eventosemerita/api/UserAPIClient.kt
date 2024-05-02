package com.example.android_eventosemerita.api

import android.content.Context
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.login.EncryptionUtil
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONException
import org.json.JSONObject

class UserAPIClient(private val context: Context) {
    private val url: String? = "https://x2t55z6x-3000.uks1.devtunnels.ms"

    /**
     * Función que actualiza el nombre y el email
     */
    fun updateUser(userId: Int,nombre: String, email: String, callback: Callback.MyCallback<User>) {
        val queue = Volley.newRequestQueue(context)
        val url = "$url/usuarios/$userId"

        val userData = JSONObject()
        try {
            userData.put("nombre", nombre)
            userData.put("email", email)
        } catch (e: JSONException) {
            callback.onError(null)
            return
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.PUT, url, userData,
            { response ->
                try {
                    val user = Gson().fromJson(response.toString(), User::class.java)
                    callback.onSuccess(user)
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
     * Modifica la lista de like
     */
    fun updateUserList(userId: Int, eventId: Int, addToFavorites: Boolean, callback: Callback.MyCallback<Boolean>) {
        val queue = Volley.newRequestQueue(context)
        val url = "$url/usuarios/list/$userId"

        val userData = JSONObject()
        try {
            userData.put("eventId", eventId)
            userData.put("addToFavorites", addToFavorites)
        } catch (e: JSONException) {
            callback.onError(false)
            return
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.PUT, url, userData,
            { response ->
                try {
                    val isLiked = response.getBoolean("isLiked")
                    callback.onSuccess(isLiked)
                } catch (e: JsonSyntaxException) {
                    callback.onError(false)
                }
            },
            { error ->
                callback.onError(false)
            })


        queue.add(jsonObjectRequest)
    }
    /**
     * Encuentra si el evento tiene un like por el usuario
     */
    fun isLikedEvent(userId: Int, eventId: Int,callback: Callback.MyCallback<Boolean>) {
        val queue = Volley.newRequestQueue(context)
        val url = "$url/usuarios/like/$userId?eventId=$eventId"

        val userData = JSONObject()
        try {
            userData.put("eventId", eventId)
        } catch (e: JSONException) {
            callback.onError(null)
            return
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val isLiked = response.getBoolean("isLiked")
                    callback.onSuccess(isLiked)
                } catch (e: JSONException) {
                    callback.onError(null)
                }
            },
            { error ->
                callback.onError(null)
            })


        queue.add(jsonObjectRequest)
    }

    /**
     * Función que devuelve un usuario con el id dado
     */
    fun getUserById(userId: Int, callback: Callback.MyCallback<User>) {
        val queue = Volley.newRequestQueue(context)
        val url = "$url/usuarios/$userId"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val user = Gson().fromJson(response.toString(), User::class.java)
                    callback.onSuccess(user)
                } catch (e: JsonSyntaxException) {
                    callback.onError(null)
                }
            },
            { error ->
                val errorString = error.toString()
                callback.onError(null)
            })

        queue.add(jsonObjectRequest)
    }

    /**
     * funcion que comprueba si el usuario existe
     */
    fun loginUser(email: String, password: String, callback: Callback.MyCallback<User>) {
        val queue = Volley.newRequestQueue(context)
        val url = "$url/usuarios/checkUser"

        val credentials = JSONObject()
        val passwordHash = EncryptionUtil.hash(password)
        try {
            credentials.put("email", email)
            credentials.put("password", passwordHash)
        } catch (e: JSONException) {
            callback.onError(null)
            return
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, credentials,
            { response ->
                try {
                    val user = Gson().fromJson(response.toString(), User::class.java)
                    callback.onSuccess(user)
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
     * Función para crear un usuario
     */
    fun createUser(name: String, email: String, password: String, callback: Callback.MyCallback<User>) {
        val queue = Volley.newRequestQueue(context)
        val url = "$url/usuarios/"

        val userData = JSONObject()
        val passwordHash = EncryptionUtil.hash(password)
        val eventsLikeList = ArrayList<String>()
        try {
            userData.put("id", 0)
            userData.put("nombre", name)
            userData.put("email", email)
            userData.put("password", passwordHash)
            userData.put("eventsLikeList", eventsLikeList)
        } catch (e: JSONException) {
            callback.onError(null)
            return
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, userData,
            { response ->
                try {
                    val user = Gson().fromJson(response.toString(), User::class.java)
                    callback.onSuccess(user)
                } catch (e: JsonSyntaxException) {
                    callback.onError(null)
                }
            },
            { error ->
                print(error)
                callback.onError(null)
            })

        queue.add(jsonObjectRequest)
    }
    /**
     * Función que me devuelve la lista de eventos favoritos
     */
    fun getFavEventsList(userId: Int, callback: Callback.MyCallback<List<Event>>){
        val queue = Volley.newRequestQueue(context)
        val url = "$url/usuarios/likeList/$userId"

        val jsonObjectRequest = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val eventList = Gson().fromJson(response, Array<Event>::class.java).toList()
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




}