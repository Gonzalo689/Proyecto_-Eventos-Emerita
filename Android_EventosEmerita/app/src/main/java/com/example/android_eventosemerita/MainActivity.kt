package com.example.android_eventosemerita

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.android_eventosemerita.databinding.ActivityMainBinding
import com.example.android_eventosemerita.fragments_nav.Home
import com.example.android_eventosemerita.fragments_nav.Search


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isBottomNavVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideNavKeyboard()

        loadFragment(Home())

        setupBottomNavigationView()
    }


    private fun setupBottomNavigationView() {
        binding.nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(Home())
                    true
                }
                R.id.search -> {
                    loadFragment(Search())
                    true
                }

                else -> false
            }
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun hideNavKeyboard(){
        binding.root.getViewTreeObserver().addOnGlobalLayoutListener(OnGlobalLayoutListener {
            val heightDiff: Int = binding.root.getRootView().getHeight() - binding.root.getHeight()
            if (heightDiff > dpToPx( 200) || isBottomNavVisible) {
                binding.nav.visibility = View.GONE
            } else {
                binding.nav.visibility = View.VISIBLE
            }
        })
    }
    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
    fun setBottomNavVisibility(visible: Boolean) {
        isBottomNavVisible = visible
    }

}