package com.example.android_eventosemerita.login

import java.security.MessageDigest

object EncryptionUtil {
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
}