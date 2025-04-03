package com.paudam.colzone.BodyApp

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class SharedVM: ViewModel() {
    private var _userActual: String?=null
    val userActual: String?
        get()=_userActual

    //Obtener user actual
    fun userActual(correo: String) {
        _userActual = correo
    }

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

}