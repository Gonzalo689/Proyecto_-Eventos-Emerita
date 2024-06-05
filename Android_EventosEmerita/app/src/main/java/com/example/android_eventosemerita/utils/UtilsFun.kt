package com.example.android_eventosemerita.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Patterns
import android.util.Base64
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.model.Coment
import com.example.android_eventosemerita.api.model.Event
import java.io.ByteArrayOutputStream
import com.example.android_eventosemerita.utils.UtilsConst.REMEMBER
import com.example.android_eventosemerita.utils.UtilsConst.USER_ID
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.Calendar

/**
 * Clase que contiene funciones útiles para la aplicación.
 */
object UtilsFun {

    /**
     * Valida el formato de un correo electrónico.
     *
     * @param email Correo electrónico a validar.
     * @return `true` si el formato es válido, `false` en caso contrario.
     */
    fun validateEmail(email: String): Boolean {
        return email.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }
    }

    /**
     * Guarda el identificador de usuario en las preferencias compartidas.
     *
     * @param context Contexto de la aplicación.
     * @param user Identificador de usuario.
     */
    fun remenberUser(context: Context, user:Int) {
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean(REMEMBER, true)
        editor.putInt(USER_ID, user)
        editor.apply()
    }

    /**
     * Reduce la calidad de una imagen representada en un arreglo de bytes.
     *
     * @param image Arreglo de bytes que representa la imagen.
     * @return Representación en base64 de la imagen con reducción de calidad.
     */
    fun lowerQuality(image: ByteArray?):String {
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image!!.size)

            val compressedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

            val outputStream = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    /**
     * Genera un hash SHA-256 de un valor de entrada.
     *
     * @param value Valor de entrada para el cual se generará el hash.
     * @return Hash generado.
     */
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

    /**
     * Agrega o cancela una notificación para un evento dependiendo de su fecha y la configuración de notificaciones.
     *
     * @param isAdd Indica si se debe agregar (`true`) o cancelar (`false`) la notificación.
     * @param event Evento para el cual se realizará la acción de notificación.
     * @param context Contexto de la aplicación.
     */
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

    /**
     * Convierte un comentario a formato JSON.
     *
     * @param coment Comentario a convertir.
     * @return Objeto JSON que representa el comentario.
     */
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

    /**
     * Convierte los dp a px para un contexto dado.
     *
     * @param context Contexto de la aplicación.
     * @return Valor en píxeles correspondiente a la conversión de dp a px.
     */
     fun dpToPx(context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (UtilsConst.DP_KEYBOARD * density).toInt()
    }

    

}