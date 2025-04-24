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
import com.paudam.colzone.BodyApp.User

class UsersAdapterSugerencia(
    private var usersList: MutableList<User>,
    private val itemClickListener: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapterSugerencia.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewUserName: TextView = view.findViewById(R.id.textViewNomUser)
        val imageViewUser: ImageView = view.findViewById(R.id.imatgeUser)
        val btnAddUser: ImageButton = view.findViewById(R.id.btnAddUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_users_search, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val targetUser = usersList[position]

        holder.textViewUserName.text = targetUser.name

        val defaultImage = R.drawable.user_icon_free
        val imageUrl = targetUser.imageUrl

        if (imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(defaultImage)
                .error(defaultImage)
                .into(holder.imageViewUser)
        } else {
            holder.imageViewUser.setImageResource(defaultImage)
        }

        holder.itemView.setOnClickListener {
            itemClickListener(targetUser)
        }

        // Establecer el estado inicial del botón
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let { currentUser ->
            val db = FirebaseFirestore.getInstance()
            val currentUserRef = db.collection("Users").document(currentUser.uid)

            currentUserRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val seguidos = document.get("seguidos") as? List<String> ?: emptyList()
                    val yaLoSigue = seguidos.contains(targetUser.userId)
                    targetUser.seguidos = yaLoSigue

                    val iconRes = if (yaLoSigue) R.drawable.addusersearchtrue else R.drawable.addusersearch
                    holder.btnAddUser.setImageResource(iconRes)
                }
            }
        }

        // Click en botón "seguir"
        holder.btnAddUser.setOnClickListener {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser?.let { currentUser ->
                val db = FirebaseFirestore.getInstance()
                val currentUserRef = db.collection("Users").document(currentUser.uid)

                currentUserRef.get().addOnSuccessListener { document ->
                    val seguidos = document.get("seguidos") as? List<String> ?: emptyList()
                    val yaLoSigue = seguidos.contains(targetUser.userId)

                    if (yaLoSigue) {
                        // Dejar de seguir
                        currentUserRef.update("seguidos", FieldValue.arrayRemove(targetUser.userId))
                            .addOnSuccessListener {
                                targetUser.seguidos = false
                                holder.btnAddUser.setImageResource(R.drawable.addusersearch)
                                Toast.makeText(holder.itemView.context, "Has dejado de seguir a ${targetUser.name}", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(holder.itemView.context, "Error al dejar de seguir", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Empezar a seguir
                        currentUserRef.update("seguidos", FieldValue.arrayUnion(targetUser.userId))
                            .addOnSuccessListener {
                                targetUser.seguidos = true
                                holder.btnAddUser.setImageResource(R.drawable.addusersearchtrue)
                                Toast.makeText(holder.itemView.context, "Ahora sigues a ${targetUser.name}", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(holder.itemView.context, "Error al seguir usuario", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = usersList.size

    fun updateList(newList: List<User>) {
        usersList.clear()
        usersList.addAll(newList)
        notifyDataSetChanged()
    }
}
