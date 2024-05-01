package com.example.android_eventosemerita.fragments_nav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android_eventosemerita.R
import com.example.android_eventosemerita.databinding.FragmentFavoriteBinding
import com.example.android_eventosemerita.databinding.FragmentProfileBinding
import com.example.android_eventosemerita.login.SignIn
import com.example.android_eventosemerita.utils.ImageCircle
import com.squareup.picasso.Picasso


class Profile : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Picasso.get()
            .load(R.drawable.prueba)
            .transform(ImageCircle())
            .into(binding.profileimage)
        binding.quitarRemenber.setOnClickListener(View.OnClickListener {
            forgotUser()
        })

    }
    fun forgotUser(){
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = preferences.edit()
        editor.putBoolean(SignIn.REMEMBER, false)
        editor.putInt(SignIn.USER_ID, 0)
        editor.apply()
    }
}