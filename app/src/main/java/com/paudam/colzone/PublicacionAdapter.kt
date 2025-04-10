package com.paudam.colzone.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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
        val btnEnviarComentario: ImageView = view.findViewById(R.id.imageView2)
        val textComentario1: TextView = view.findViewById(R.id.textComentario1)
        val textComentario2: TextView = view.findViewById(R.id.textComentario2)
        val imageViewProducte: ImageView = view.findViewById(R.id.imatgeProducte)
        val btnFavorito: ImageButton = view.findViewById(R.id.btnFavorito)
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

        // Listener para el botón de favorito
        holder.btnFavorito.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let { user ->
                val db = FirebaseFirestore.getInstance()
                val publiRef = db.collection("publicaciones").document(publicacion.publiId)
                val userDocRef = db.collection("Users").document(user.uid)

                // Verificar si la publicación ya está en los favoritos del usuario
                userDocRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val favoritos = document.get("favoritos") as? List<String> ?: emptyList()
                        val isFavorite = favoritos.contains(publicacion.publiId)

                        if (isFavorite) {
                            // Eliminar de favoritos
                            userDocRef.update("favoritos", FieldValue.arrayRemove(publicacion.publiId))
                                .addOnSuccessListener {
                                    // Actualizar el ícono y el estado de la publicación
                                    holder.btnFavorito.setImageResource(R.drawable.ic_favorite_border)
                                    publicacion.favs = false
                                    Toast.makeText(holder.itemView.context, "Publicación eliminada de favoritos", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Favoritos", "Error al eliminar de favoritos", e)
                                    Toast.makeText(holder.itemView.context, "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Agregar a favoritos
                            userDocRef.update("favoritos", FieldValue.arrayUnion(publicacion.publiId))
                                .addOnSuccessListener {
                                    // Actualizar el ícono y el estado de la publicación
                                    holder.btnFavorito.setImageResource(R.drawable.ic_favorite_border_liked)
                                    publicacion.favs = true
                                    Toast.makeText(holder.itemView.context, "Publicación agregada a favoritos", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Favoritos", "Error al agregar a favoritos", e)
                                    Toast.makeText(holder.itemView.context, "Error al agregar a favoritos", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }
        }

        // Establecer el estado inicial del botón de favorito según los favoritos del usuario
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("Users").document(user.uid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val favoritos = document.get("favoritos") as? List<String> ?: emptyList()
                    val isFavorite = favoritos.contains(publicacion.publiId)
                    publicacion.favs = isFavorite
                    val iconRes = if (isFavorite) R.drawable.ic_favorite_border_liked else R.drawable.ic_favorite_border
                    holder.btnFavorito.setImageResource(iconRes)
                }
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
