package com.example.android_eventosemerita.controller

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.ComentAPIClient
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.Coment
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.databinding.FeedComentsBinding
import com.example.android_eventosemerita.utils.Image.Companion.decodeBase64ToFile
import com.example.android_eventosemerita.utils.Image.Companion.imgFile
import com.example.android_eventosemerita.utils.Image.Companion.imgPred
import com.example.android_eventosemerita.utils.UtilsConst.userRoot
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.text.SimpleDateFormat
import java.util.*

/**
 * AdapterComents es un adaptador de RecyclerView que maneja la vista de comentarios en una lista.
 *
 * @param coments Lista de comentarios que se mostrarán.
 * @param text TextView que se muestra cuando no hay comentarios.
 */
class AdapterComents (private var coments: ArrayList<Coment>, private val text: TextView) : RecyclerView.Adapter<AdapterComents.FeedViewComents>() {

    /**
     * onCreateViewHolder se llama cuando RecyclerView necesita un nuevo ViewHolder
     * de tipo FeedViewComents para representar un elemento.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewComents {
        val context = parent.context
        val binding = FeedComentsBinding.inflate(LayoutInflater.from(context), parent, false)
        val userAPIClient = UserAPIClient(context)
        val comentAPIClient = ComentAPIClient(context)
        return FeedViewComents(context, binding, userAPIClient,comentAPIClient)
    }

    /**
     * onBindViewHolder se llama para mostrar los datos en la posición especificada.
     */
    override fun onBindViewHolder(feedHolder: FeedViewComents, position: Int) {
        feedHolder.bind(coments[position])
    }

    /**
     * getItemCount devuelve el número total de elementos en el conjunto de datos.
     */
    override fun getItemCount(): Int {
        return coments.size
    }

    /**
     * updateComents actualiza la lista de comentarios y notifica al adaptador que los datos han cambiado.
     *
     * @param newEventsList Nueva lista de comentarios que se mostrarán.
     */
    fun updateComents(newEventsList: ArrayList<Coment>) {
        coments = newEventsList
        notifyDataSetChanged()
    }

    /**
     * updateOneComent agrega un nuevo comentario a la lista y notifica al adaptador que los datos han cambiado.
     *
     * @param coment Nuevo comentario que se agregará a la lista.
     */
    fun updateOneComent(coment: Coment) {
        coments.add(0,coment)
        notifyDataSetChanged()
    }

    /**
     * deleteComent elimina un comentario de la lista y notifica al adaptador que los datos han cambiado.
     *
     * @param coment Comentario que se eliminará de la lista.
     */
    fun deleteComent(coment: Coment) {
        coments.remove(coment)
        if (coments.size<=0){
            text.visibility = View.VISIBLE
        }
        notifyDataSetChanged()
    }

    /**
     * FeedViewComents es una clase interna que actúa como ViewHolder para mostrar
     * cada elemento de comentario en el RecyclerView.
     */
    inner class FeedViewComents(private val context: Context,
                                private val binding: FeedComentsBinding,
                                private val userAPIClient: UserAPIClient,
                                private val comentAPIClient: ComentAPIClient
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * bind vincula los datos de un comentario específico a la vista correspondiente.
         *
         * @param coment Comentario que se va a mostrar.
         */
        fun bind(coment: Coment) {
            binding.remove.visibility = View.GONE
            getUser(coment)
            showRemove(coment)
            removeComent(coment)

            binding.textComent.text = coment.texto
            val dateTimeDifference = calculateDateTimeDifference(coment.fecha)
            binding.date.text = dateTimeDifference


        }
        /**
         * removeComent define la lógica para eliminar un comentario.
         *
         * @param coment Comentario que se eliminará.
         */
        private fun removeComent(coment: Coment){
            binding.remove.setOnClickListener{
                comentAPIClient.deleteComent(coment.id, object :Callback.MyCallback<String>{
                    override fun onSuccess(data: String) {
                        deleteComent(coment)
                        Toast.makeText(context, context.getString(R.string.coment_deleted), Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(errorMsg: String?) {
                        Toast.makeText(context, context.getString(R.string.error_coment_deleted), Toast.LENGTH_LONG).show()
                    }

                })
            }
        }

        /**
         * showRemove muestra el botón de eliminar para el comentario del usuario actual.
         *
         * @param coment Comentario actual.
         */
        private fun showRemove(coment: Coment){
            userRoot?.let {
                    if (coment.idUser == it.id){
                    binding.remove.visibility = View.VISIBLE
                }
            }
        }

        /**
         * getUser devuelve el usuario para obtener sus datos
         */
        private fun getUser(coment: Coment){
            userAPIClient.getUserById(coment.idUser, object : Callback.MyCallback<User>{
                override fun onSuccess(data: User) {
                    dataUser(data)
                }

                override fun onError(errorMsg: User?) {
                    userAPIClient.getUserById(coment.idUser, this)
                }

            })

        }

        /**
         * Funcion que vincula los datos del usuario con la vista
         * @param user usuario del comentario
         */
        private fun dataUser(user: User){
            val profileImage = user.profilePicture
            val imageUser = binding.imageUser
            if (profileImage.isNotEmpty()){
                val file = decodeBase64ToFile(profileImage, context, user.id.toString())
                imgFile(file, imageUser)
            }else{
                imgPred(imageUser)
            }

            binding.textNameUSer.text = context.getString(R.string.name_coment,user.nombre )
        }

        /**
         * Función que devuelve la diferencia con la fecha actual de cuando se efectuo el comentario
         * @param fecha fecha que se inicio el comentario
         */
        private fun calculateDateTimeDifference(fecha: String): String {
            // Versiones superiores a version code.0
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val commentDateTime = LocalDateTime.parse(fecha, formatter)
                val currentDateTime = LocalDateTime.now()

                val days = ChronoUnit.DAYS.between(commentDateTime, currentDateTime)
                val hours = ChronoUnit.HOURS.between(commentDateTime, currentDateTime) % 24
                val minutes = ChronoUnit.MINUTES.between(commentDateTime, currentDateTime) % 60
                val seconds = ChronoUnit.SECONDS.between(commentDateTime, currentDateTime) % 60

                return getTimeAgoString(days, hours, minutes, seconds)
            } else {
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val commentDateTime = formatter.parse(fecha)
                val currentDateTime = Date()
                val diffInMillis = currentDateTime.time - commentDateTime!!.time
                val days = diffInMillis / (1000 * 60 * 60 * 24)
                val hours = (diffInMillis / (1000 * 60 * 60)) % 24
                val minutes = (diffInMillis / (1000 * 60)) % 60
                val seconds = (diffInMillis / 1000) % 60

                return getTimeAgoString(days, hours, minutes, seconds)
            }

        }

        /**
         * Devuelve el string que se mostrara que contendra la diferencia de tiempo
         */
        fun getTimeAgoString(days: Long, hours: Long, minutes: Long, seconds: Long): String {
            return when {
                days > 0 -> context.getString(R.string.days_ago, days)
                hours > 0 -> context.getString(R.string.hours_ago, hours)
                minutes > 0 -> context.getString(R.string.minutes_ago, minutes)
                else -> context.getString(R.string.seconds_ago, seconds)
            }
        }

    }
}