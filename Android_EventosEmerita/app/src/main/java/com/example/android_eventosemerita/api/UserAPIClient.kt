package com.example.android_eventosemerita.api

import android.content.Context
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.login.EncryptionUtil
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONException
import org.json.JSONObject

class UserAPIClient(private val context: Context) {
    private val url: String? = "https://x2t55z6x-3000.uks1.devtunnels.ms"

    fun updateUser(userId: Int,nombre: String, email: String, callback: Callback.MyCallback<User>) {
        val queue = Volley.newRequestQueue(context)
        val url = "$url/usuarios/$userId"

        val userData = JSONObject()
        try {
            userData.put("id", userId)
            userData.put("nombre", nombre)
            userData.put("email", email)
        } catch (e: JSONException) {
            callback.onError("Error creating JSON")
            return
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.PUT, url, userData,
            { response ->
                try {
                    val user = Gson().fromJson(response.toString(), User::class.java)
                    callback.onSuccess(user)
                } catch (e: JsonSyntaxException) {
                    callback.onError("Error parsing JSON")
                }
            },
            { error ->
                val errorString = error.toString()
                callback.onError(errorString)
            })

        queue.add(jsonObjectRequest)
    }
    fun getUserById(userId: Int, callback: Callback.MyCallback<User>) {
        val queue = Volley.newRequestQueue(context)
        val url = "$url/usuarios/$userId"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val user = Gson().fromJson(response.toString(), User::class.java)
                    callback.onSuccess(user)
                } catch (e: JsonSyntaxException) {
                    callback.onError("Error parsing JSON")
                }
            },
            { error ->
                val errorString = error.toString()
                callback.onError(errorString)
            })

        queue.add(jsonObjectRequest)
    }
    fun loginUser(email: String, password: String, callback: Callback.MyCallback<User>) {
        val queue = Volley.newRequestQueue(context)
        val url = "$url/usuarios/checkUser"

        val credentials = JSONObject()
        val passwordHash = EncryptionUtil.hash(password)
        try {
            credentials.put("email", email)
            credentials.put("password", passwordHash)
        } catch (e: JSONException) {
            callback.onError("Error creating JSON")
            return
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, credentials,
            { response ->
                try {
                    val user = Gson().fromJson(response.toString(), User::class.java)
                    callback.onSuccess(user)
                } catch (e: JsonSyntaxException) {
                    callback.onError("Error parsing JSON")
                }
            },
            { error ->
                val errorString = error.toString()
                callback.onError(errorString)
            })

        queue.add(jsonObjectRequest)
    }

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
            callback.onError("Error creating JSON")
            return
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, userData,
            { response ->
                try {
                    val user = Gson().fromJson(response.toString(), User::class.java)
                    callback.onSuccess(user)
                } catch (e: JsonSyntaxException) {
                    callback.onError("Error parsing JSON")
                }
            },
            { error ->
                var errorMsg = "Error creating user"
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    errorMsg = String(error.networkResponse.data)
                }
                callback.onError(errorMsg)
            })

        queue.add(jsonObjectRequest)
    }




}