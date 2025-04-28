package com.paudam.colzone.BodyApp.Perfil.CrearPubli

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class CrearPublicacionVM: ViewModel() {

    fun insertPublicacion(
        db: FirebaseFirestore,
        userName: String,
        userId: String,
        title: String,
        rank: Int,
        commentsId: Timestamp,
        context: Context
    ) {
        val publiRef = db.collection("publicaciones").document() // Genera un ID único

        val publiData = hashMapOf(
            "publiId" to publiRef.id, // Usamos el ID del documento como publiId
            "userName" to userName,
            "userId" to userId,
            "title" to title,
            "rank" to rank,
            "favs" to false,
            "commentsId" to commentsId,
            "date" to Timestamp.now() // Guardamos la fecha actual
        )

        publiRef.set(publiData)
            .addOnSuccessListener {
                Toast.makeText(context, "Publicación creada exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}