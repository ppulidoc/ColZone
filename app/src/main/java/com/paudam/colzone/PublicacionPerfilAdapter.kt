package com.paudam.colzone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.paudam.colzone.R
import com.paudam.colzone.BodyApp.Publicacion

class PublicacionPerfilAdapter(
    private var publicacionesList: MutableList<Publicacion>,
    private val itemClickListener: (Publicacion) -> Unit
) : RecyclerView.Adapter<PublicacionPerfilAdapter.PublicacionViewHolder>() {

    class PublicacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewProducte: ImageView = view.findViewById(R.id.imatgeProducte)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_publicacio_perfil, parent, false)
        return PublicacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val publicacion = publicacionesList[position]

        // Cargar imagen con Glide
        val defaultImage = R.drawable.default_example
        val imageUrl = publicacion.imageUrl
        if (imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(defaultImage)
                .error(defaultImage)
                .into(holder.imageViewProducte)
        } else {
            holder.imageViewProducte.setImageResource(defaultImage)
        }

        // Mostrar título de la publicación (si existe)
        holder.textViewTitle.text = publicacion.title ?: "Sin título"



        // Listener para clic en el item
        holder.itemView.setOnClickListener {
            itemClickListener(publicacion)
        }
    }

    override fun getItemCount(): Int = publicacionesList.size

    fun updateList(newList: List<Publicacion>) {
        publicacionesList.clear()
        publicacionesList.addAll(newList)
        notifyDataSetChanged()
    }
}
