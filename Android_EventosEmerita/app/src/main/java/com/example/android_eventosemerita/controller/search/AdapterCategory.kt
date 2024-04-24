package com.example.android_eventosemerita.controller.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_eventosemerita.api.model.Category
import com.example.android_eventosemerita.databinding.FeedCategoryBinding
import com.example.android_eventosemerita.fragments_nav.Search

class AdapterCategory (private var categories: ArrayList<Category>, private val searchActivity: Search) : RecyclerView.Adapter<AdapterCategory.FeedViewCategory>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewCategory {

        val binding = FeedCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewCategory(binding)
    }

    override fun onBindViewHolder(feedHolder: FeedViewCategory, position: Int) {
        feedHolder.bind(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class FeedViewCategory(private val binding: FeedCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.textCategory.text = category.name
            binding.imageCategory.setImageResource(category.image)
            binding.layautCategory.setOnClickListener(View.OnClickListener {
                searchActivity.EventsCategory(category.busqueda)
            })

        }

    }
}