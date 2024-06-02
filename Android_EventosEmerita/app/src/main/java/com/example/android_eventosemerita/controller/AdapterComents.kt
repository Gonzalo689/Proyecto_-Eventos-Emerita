package com.example.android_eventosemerita.controller

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.activity.MainActivity
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

class AdapterComents (private var coments: ArrayList<Coment>, private val text: TextView) : RecyclerView.Adapter<AdapterComents.FeedViewComents>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewComents {
        val context = parent.context
        val binding = FeedComentsBinding.inflate(LayoutInflater.from(context), parent, false)
        val userAPIClient = UserAPIClient(context)
        val comentAPIClient = ComentAPIClient(context)
        return FeedViewComents(context, binding, userAPIClient,comentAPIClient)
    }

    override fun onBindViewHolder(feedHolder: FeedViewComents, position: Int) {
        feedHolder.bind(coments[position])
    }

    override fun getItemCount(): Int {
        return coments.size
    }
    fun updateComents(newEventsList: ArrayList<Coment>) {
        coments = newEventsList
        notifyDataSetChanged()
    }
    fun updateOneComent(coment: Coment) {
        coments.add(0,coment)
        notifyDataSetChanged()
    }
    fun deleteComent(coment: Coment) {
        coments.remove(coment)
        if (coments.size<=0){
            text.visibility = View.VISIBLE
        }
        notifyDataSetChanged()
    }
    inner class FeedViewComents(private val context: Context,
                                private val binding: FeedComentsBinding,
                                private val userAPIClient: UserAPIClient,
                                private val comentAPIClient: ComentAPIClient
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coment: Coment) {

            getUser(coment)
            showRemove(coment)
            removeComent(coment)

            binding.textComent.text = coment.texto
            val dateTimeDifference = calculateDateTimeDifference(coment.fecha)
            binding.date.text = dateTimeDifference


        }
        private fun removeComent(coment: Coment){
            binding.remove.setOnClickListener{
                comentAPIClient.deleteComent(coment.id, object :Callback.MyCallback<String>{
                    override fun onSuccess(data: String) {
                        deleteComent(coment)
                        Toast.makeText(context, "Comentario eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(errorMsg: String?) {
                        Toast.makeText(context, "Error al eliminar el comentario: $errorMsg", Toast.LENGTH_LONG).show()
                    }

                })
            }
        }
        private fun showRemove(coment: Coment){
            if (coment.idUser == userRoot!!.id){
                binding.remove.visibility = View.VISIBLE
            }
        }

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
        private fun dataUser(user: User){
            val profileImage = user.profilePicture
            val imageUser = binding.imageUser
            if (profileImage.isNotEmpty()){
                val file = decodeBase64ToFile(profileImage, context, user.id.toString())
                imgFile(file, imageUser)
            }else{
                imgPred(imageUser)
            }
            binding.textNameUSer.text = user.nombre
        }


        private fun calculateDateTimeDifference(fecha: String): String {
            // Versiones superiores a version code.0
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val commentDateTime = LocalDateTime.parse(fecha, formatter)
                val currentDateTime = LocalDateTime.now()

                val days = ChronoUnit.DAYS.between(commentDateTime, currentDateTime)
                val hours = ChronoUnit.HOURS.between(commentDateTime, currentDateTime) % 24
                val minutes = ChronoUnit.MINUTES.between(commentDateTime, currentDateTime) % 60
                val seconds = ChronoUnit.SECONDS.between(commentDateTime, currentDateTime) % 60

                println("Fecha: $days -- $hours -- $minutes -- $seconds")

                when {
                    days > 0 -> "$days days ago"
                    hours > 0 -> "$hours hours ago"
                    minutes > 0 -> "$minutes minutes ago"
                    else -> "$seconds seconds ago"
                }
            } else {
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val commentDateTime = formatter.parse(fecha)
                val currentDateTime = Date()
                val diffInMillis = currentDateTime.time - commentDateTime!!.time
                val days = diffInMillis / (1000 * 60 * 60 * 24)
                val hours = (diffInMillis / (1000 * 60 * 60)) % 24
                val minutes = (diffInMillis / (1000 * 60)) % 60
                val seconds = (diffInMillis / 1000) % 60

                println("Fecha: $days -- $hours -- $minutes -- $seconds")

                when {
                    days > 0 -> "$days days ago"
                    hours > 0 -> "$hours hours ago"
                    minutes > 0 -> "$minutes minutes ago"
                    else -> "$seconds seconds ago"
                }
            }
        }

    }
}