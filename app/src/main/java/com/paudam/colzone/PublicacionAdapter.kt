package com.paudam.colzone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.paudam.colzone.R
import com.paudam.colzone.BodyApp.Publicacion

class PublicacionAdapter(
    private var publicacionesList: MutableList<Publicacion>,
    private val itemClickListener: (Publicacion) -> Unit
) : RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder>() {

    class PublicacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewUserName: TextView = view.findViewById(R.id.textViewNomLabel)
        val textViewTitle: TextView = view.findViewById(R.id.textNomPublicacio)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val editTextComentario: EditText = view.findViewById(R.id.editTextComentarios)
        val btnEnviarComentario: ImageView = view.findViewById(R.id.imageView2)
        val textComentario1: TextView = view.findViewById(R.id.textComentario1)
        val textComentario2: TextView = view.findViewById(R.id.textComentario2)
        val imageViewProducte: ImageView = view.findViewById(R.id.imatgeProducte) // Imagen del producto
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_publicacio_home, parent, false)
        return PublicacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val publicacion = publicacionesList[position]

        holder.textViewUserName.text = publicacion.userName
        holder.textViewTitle.text = publicacion.title
        holder.ratingBar.rating = publicacion.rank.toFloat()

        // Cargar imagen con Glide
        val defaultImage = R.drawable.default_example // Imagen local en res/drawable
        val imageUrl = publicacion.imageUrl

        if (imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl) // Carga desde URL
                .placeholder(defaultImage) // Imagen mientras carga
                .error(defaultImage) // Imagen en caso de error
                .into(holder.imageViewProducte)
        } else {
            holder.imageViewProducte.setImageResource(defaultImage) // Usa la imagen predeterminada
        }

        // Listener para clic en el item
        holder.itemView.setOnClickListener {
            itemClickListener(publicacion)
        }

        // Listener para botón de enviar comentario
        holder.btnEnviarComentario.setOnClickListener {
            val nuevoComentario = holder.editTextComentario.text.toString()
            if (nuevoComentario.isNotEmpty()) {
                holder.textComentario1.text = holder.textComentario2.text
                holder.textComentario2.text = nuevoComentario
                holder.editTextComentario.text.clear()
            }
        }
    }

    override fun getItemCount(): Int = publicacionesList.size

    fun updateList(newList: List<Publicacion>) {
        publicacionesList.clear()
        publicacionesList.addAll(newList)
        notifyDataSetChanged()
    }
}
