package com.example.android_eventosemerita.controller.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

import com.example.android_eventosemerita.api.model.Category
import com.example.android_eventosemerita.databinding.FeedCategoryBinding
import com.example.android_eventosemerita.fragments_nav.Search
import com.example.android_eventosemerita.fragments_nav.Search.Companion.categoryPair

/**
 * AdapterCategory es un adaptador de RecyclerView que maneja la vista de las categorías
 * disponibles en la pantalla de búsqueda.
 *
 * @param categories Lista de categorías que se mostrarán en la pantalla de búsqueda.
 * @param searchActivity Instancia de Search, utilizada para llamar a la función eventsCategory
 * y actualizar la actividad de búsqueda.
 * @param button Botón que se mostrará cuando se seleccione una categoría.
 */
class AdapterCategory (private var categories: ArrayList<Category>, private val searchActivity: Search, button: Button) : RecyclerView.Adapter<AdapterCategory.FeedViewCategory>() {
    val button = button

    /**
     * onCreateViewHolder se llama cuando se necesita crear una nueva vista de elemento de la
     * lista de categorías.
     *
     * @param parent El ViewGroup en el que se inflará la nueva vista.
     * @param viewType El tipo de la vista, que se utiliza para crear diferentes tipos de vistas
     * de elementos.
     * @return FeedViewCategory, una instancia de ViewHolder que contiene la vista de un elemento
     * de categoría.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewCategory {

        val binding = FeedCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewCategory(binding)
    }
    /**
     * onBindViewHolder se llama cuando se necesita vincular los datos de una categoría específica
     * en una posición dada al FeedViewCategory correspondiente.
     *
     * @param feedHolder El FeedViewCategory en el que se vincularán los datos.
     * @param position La posición de la categoría en la lista de categorías.
     */
    override fun onBindViewHolder(feedHolder: FeedViewCategory, position: Int) {
        feedHolder.bind(categories[position])
    }

    /**
     * getItemCount devuelve el número total de elementos en la lista de categorías.
     *
     * @return El número total de elementos en la lista de categorías.
     */
    override fun getItemCount(): Int {
        return categories.size
    }

    /**
     * FeedViewCategory es una clase interna que actúa como ViewHolder para los elementos de la
     * lista de categorías. Contiene un método bind que se utiliza para vincular los datos de una
     * categoría específica a la vista correspondiente.
     *
     * @param binding Binding generado para la vista del elemento de la lista de categorías.
     */
    inner class FeedViewCategory(private val binding: FeedCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * bind vincula los datos de una categoría específica a la vista correspondiente.
         *
         * @param category Categoría que se va a mostrar.
         */
        fun bind(category: Category) {

            binding.textCategory.text = category.name
            binding.imageCategory.setImageResource(category.image)
            binding.layautCategory.setOnClickListener{
                searchActivity.eventsCategory(category.busqueda)
                button.visibility = View.VISIBLE
                button.text = category.name
                categoryPair = Pair(true,category.busqueda)
            }

        }

    }
}