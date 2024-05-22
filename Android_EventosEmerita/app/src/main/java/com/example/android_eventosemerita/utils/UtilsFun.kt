package com.example.android_eventosemerita.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Patterns
import android.util.Base64
import android.widget.EditText
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.activity.SplashScreen
import com.example.android_eventosemerita.api.model.Coment
import com.example.android_eventosemerita.api.model.Event
import java.io.ByteArrayOutputStream
import com.example.android_eventosemerita.utils.UtilsConst.REMEMBER
import com.example.android_eventosemerita.utils.UtilsConst.USER_ID
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.Calendar

object UtilsFun {
    fun validateEmail(email: String): Boolean {
        return email.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }
    }
    fun remenberUser(context: Context, user:Int) {
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean(REMEMBER, true)
        editor.putInt(USER_ID, user)
        editor.apply()
    }
    fun lowerQuality(image: ByteArray?):String {
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image!!.size)

            val compressedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

            val outputStream = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }
    fun hash(value: String): String? {
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(value.toByteArray(charset("UTF-8")))
            val hexString = StringBuilder()
            for (hashByte in hashBytes) {
                val hex = Integer.toHexString(0xff and hashByte.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            return hexString.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun addNotification(isAdd:Boolean, event: Event, context: Context){
        val mainActivity = context as MainActivity
        val calendar = Calendar.getInstance()
        val yearNow = calendar.get(Calendar.YEAR)
        val monthNow = calendar.get(Calendar.MONTH) + 1
        val dayNow = calendar.get(Calendar.DAY_OF_MONTH)
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val notif = preferences.getBoolean(UtilsConst.NOTIF,true)
        val datenow= "$yearNow-$monthNow-$dayNow"
        if (event.checkDate(datenow) == 1 && notif){
            if (isAdd){
                mainActivity.sheduleNotification(event)
            }else{
                mainActivity.cancelNotification(context,event.eventId)
            }
        }else{
            mainActivity.cancelNotification(context,event.eventId)
        }
    }
    fun comentToJson(coment: Coment): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("id", coment.id)
        jsonObject.put("texto", coment.texto)
        jsonObject.put("fecha", coment.fecha)
        jsonObject.put("idUser", coment.idUser)
        jsonObject.put("idPost", coment.idPost)

        val jsonListComents = JSONArray()
        for (nestedComent in coment.listComents) {
            jsonListComents.put(comentToJson(nestedComent))
        }
        jsonObject.put("listComents", jsonListComents)

        return jsonObject
    }
    

}