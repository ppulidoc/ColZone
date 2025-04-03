package com.paudam.colzone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
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
        val btnFavorito: ImageButton = view.findViewById(R.id.btnFavorito)
        val textComentario1: TextView = view.findViewById(R.id.textComentario1)
        val textComentario2: TextView = view.findViewById(R.id.textComentario2)
        val imageViewProducte: ImageView = view.findViewById(R.id.imatgeProducte)
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

        // Cambiar el ícono del botón de favorito según el estado de "favs"
        if (publicacion.favs) {
            holder.btnFavorito.setImageResource(R.drawable.ic_favorite_border_liked)  // Ícono de favorito marcado
        } else {
            holder.btnFavorito.setImageResource(R.drawable.ic_favorite_border)  // Ícono de favorito sin marcar
        }

        // Cargar imagen con Glide
        val defaultImage = R.drawable.default_example // Imagen local en res/drawable
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

        // Listener para clic en el item
        holder.itemView.setOnClickListener {
            itemClickListener(publicacion)
        }

        // Listener para el botón de favorito
        holder.btnFavorito.setOnClickListener {
            // Alternar el estado de "favs"
            val newFavsState = !publicacion.favs
            val updatedPublicacion = publicacion.copy(favs = newFavsState)

            // Actualizar el campo "favs" en Firestore
            val db = FirebaseFirestore.getInstance()
            val publiRef = db.collection("publicaciones").document(publicacion.publiId)

            publiRef.update("favs", newFavsState)
                .addOnSuccessListener {
                    // Cambiar el ícono dependiendo del nuevo estado de "favs"
                    if (newFavsState) {
                        holder.btnFavorito.setImageResource(R.drawable.ic_favorite_border_liked) // Ícono de favorito marcado
                    } else {
                        holder.btnFavorito.setImageResource(R.drawable.ic_favorite_border) // Ícono de favorito no marcado
                    }
                    Toast.makeText(holder.itemView.context, "Estado de favorito actualizado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Manejo de error
                    Toast.makeText(holder.itemView.context, "Error al actualizar el estado de favorito", Toast.LENGTH_SHORT).show()
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
