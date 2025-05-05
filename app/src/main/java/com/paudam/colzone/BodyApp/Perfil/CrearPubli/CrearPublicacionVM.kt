package com.paudam.colzone.BodyApp.Perfil.CrearPubli

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class CrearPublicacionVM : ViewModel() {

    fun insertPublicacion(
        db: FirebaseFirestore,
        userName: String,
        userId: String,
        title: String,
        rank: Int,
        commentsId: Timestamp,
        imageUrl: String,
        publiId: String,
        context: Context
    ) {
        val publiData = hashMapOf(
            "publiId" to publiId,
            "userName" to userName,
            "userId" to userId,
            "title" to title,
            "rank" to rank,
            "favs" to false,
            "commentsId" to commentsId,
            "date" to Timestamp.now(),
            "imageUrl" to imageUrl
        )

        db.collection("publicaciones").document(publiId).set(publiData)
            .addOnSuccessListener {
                Toast.makeText(context, "Publicación creada exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
