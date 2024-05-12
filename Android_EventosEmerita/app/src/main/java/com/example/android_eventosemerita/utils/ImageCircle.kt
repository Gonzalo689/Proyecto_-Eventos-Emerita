package com.example.android_eventosemerita.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.util.Base64
import java.io.ByteArrayOutputStream

class ImageCircle : com.squareup.picasso.Transformation {
    companion object{
        fun lowerQuality(image: ByteArray?):String {
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image!!.size)

            // Comprimir el Bitmap con la calidad deseada (0 para la calidad más baja y 100 para la calidad más alta)
            val compressedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

            // Convertir el Bitmap comprimido a una cadena Base64
            val outputStream = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        }
    }
    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)

        val x = (source.width - size) / 2
        val y = (source.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }

        val bitmap = Bitmap.createBitmap(size, size, source.config)

        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true

        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)

        squaredBitmap.recycle()
        return bitmap
    }

    override fun key(): String {
        return "circle"
    }
}