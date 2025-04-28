package com.paudam.colzone.BodyApp.Perfil

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class PerfilGeneralVM: ViewModel() {

    fun obtenirUsername(db: FirebaseFirestore, email: String, callback: (String?) -> Unit) {
        db.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val name = documents.documents[0].getString("name")
                    callback(name)
                } else {
                    callback(null) // No se encontró el usuario
                }
            }
            .addOnFailureListener { exception ->
                callback(null) // Error al obtener los datos
            }
    }

    fun contarFollows(db: FirebaseFirestore, userActual: String, callback: (Int?) -> Unit) {
        db.collection("Users")
            .document(userActual)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val seguidos = document.get("seguidos") as? List<String> ?: emptyList()
                    // Devolver el tamaño de la lista 'seguidos'
                    callback(seguidos.size)
                } else {
                    // Si no se encuentra el usuario
                    callback(0)
                }
            }
            .addOnFailureListener { exception ->
                // Si ocurre un error
                callback(null)
            }
    }



}