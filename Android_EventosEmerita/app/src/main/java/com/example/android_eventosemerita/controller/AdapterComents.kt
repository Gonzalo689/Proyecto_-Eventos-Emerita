package com.example.android_eventosemerita.controller

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.activity.MainActivity
import com.example.android_eventosemerita.api.Callback
import com.example.android_eventosemerita.api.UserAPIClient
import com.example.android_eventosemerita.api.model.Coment
import com.example.android_eventosemerita.api.model.Event
import com.example.android_eventosemerita.api.model.User
import com.example.android_eventosemerita.controller.search.AdapterSearchAll
import com.example.android_eventosemerita.databinding.FeedComentsBinding
import com.example.android_eventosemerita.databinding.FeedSearchAllBinding
import com.example.android_eventosemerita.fragments_nav.FragmentEvent
import com.example.android_eventosemerita.login.SignUp
import com.example.android_eventosemerita.utils.Image
import com.example.android_eventosemerita.utils.Image.Companion.decodeBase64ToFile
import com.example.android_eventosemerita.utils.Image.Companion.imgFile
import com.example.android_eventosemerita.utils.Image.Companion.imgPred
import com.example.android_eventosemerita.utils.UtilsConst
import com.squareup.picasso.Picasso

class AdapterComents (private var coments: ArrayList<Coment>, userAPIClient: UserAPIClient) : RecyclerView.Adapter<AdapterComents.FeedViewComents>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewComents {
        val context = parent.context
        val binding = FeedComentsBinding.inflate(LayoutInflater.from(context), parent, false)
        val userAPIClient = UserAPIClient(context)
        return FeedViewComents(context, binding, userAPIClient)
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
    inner class FeedViewComents(private val context: Context,private val binding: FeedComentsBinding, private val userAPIClient: UserAPIClient) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coment: Coment) {

            getUser(coment)
            binding.textComent.text = coment.texto

        }

        private fun getUser(coment: Coment){
            userAPIClient.getUserById(coment.idUser, object : Callback.MyCallback<User>{
                override fun onSuccess(data: User) {
                    dataUser(data)
                }

                override fun onError(errorMsg: User?) {
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


    }
}