package com.example.android_eventosemerita.utils

import com.example.android_eventosemerita.api.model.User

/**
 * Clase que contiene constantes utilizadas en la aplicación.
 */
object UtilsConst {
    // Usuario principal de la app
    var userRoot: User? = null
    // Variable para ocultar el nav cuando sale el teclado
    const val DP_KEYBOARD = 200
    // Constante del canal donde saldra la notificación
    const val CHANNEL_ID= "myChannel"
    // Saber si hay que recordar al usuario
    const val REMEMBER = "remenber"
    // Id del usuario para el inicio de sesion
    const val USER_ID = "userId"
    // Bolean para saber si tiene notificaciónes
    const val NOTIF = "notification"

    const val url: String = "https://x2t55z6x-3000.uks1.devtunnels.ms"

}