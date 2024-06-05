package com.example.android_eventosemerita.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.widget.ImageView
import com.example.android_eventosemerita.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


/**
 * Clase de utilidad para operaciones relacionadas con imágenes.
 */
class Image : com.squareup.picasso.Transformation {

    companion object{
        /**
         * Convierte una imagen en formato Uri a un array de bytes.
         *
         * @param path La Uri de la imagen.
         * @param context El contexto de la aplicación.
         * @return Un array de bytes representando la imagen.
         */
        fun convertToBytes(path: Uri, context: Context): ByteArray? {
            val format = if (Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSLESS else Bitmap.CompressFormat.WEBP
            val outputStream = ByteArrayOutputStream()
            return try {
                val inputStream: InputStream? = context.contentResolver?.openInputStream(path)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap.compress(format, 100, outputStream)
                return outputStream.toByteArray()
            } catch (e: Exception) {
                null
            }
        }

        /**
         * Decodifica una imagen en formato base64 y la guarda en un archivo.
         *
         * @param base64Image La imagen codificada en base64.
         * @param context El contexto de la aplicación.
         * @param nameFile El nombre del archivo.
         * @return El archivo generado.
         */
        fun decodeBase64ToFile(base64Image: String, context: Context, nameFile:String) :File{
            val file = File(context.cacheDir, "$nameFile.jpg")
            if (file.exists()) {
                file.delete()
            }
            val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
            file.writeBytes(decodedBytes)
            return file
        }

        /**
         * Carga una imagen desde un archivo en un ImageView utilizando Picasso.
         *
         * @param file El archivo de la imagen.
         * @param imageView El ImageView donde se cargará la imagen.
         */
        fun imgFile(file: File, imageView: ImageView){
            Picasso.get()
                .load(file)
                .transform(Image())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imageView)
        }

        /**
         * Carga una imagen predeterminada en un ImageView utilizando Picasso.
         *
         * @param imageView El ImageView donde se cargará la imagen.
         */
        fun imgPred(imageView: ImageView){
            Picasso.get()
                .load(R.drawable.event_merida)
                .transform(Image())
                .into(imageView)
        }


    }

    /**
     * Tramsforma una imagen a una imagen circular
     *
     * @param source bitmap de la imagen
     */
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