package com.paudam.colzone.InicioSesion.Registrar

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.room.Database
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class InicioSesionRegisterVM: ViewModel() {

    fun showAlert(message: String, context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun createUser(db: FirebaseFirestore, user: FirebaseUser, userName: String, userAge: Int, context: Context ){
        //crear camps de l'usuari
        val userData = hashMapOf(
            "email" to user.email,
            "name" to userName,
            "age" to userAge,
            "createdAt" to System.currentTimeMillis()
        )

        //añadir a la base de datos
        db.collection("Users")  // Accedemos a la colección "users"
            .document(user.uid)  // Usamos el UID del usuario como documento
            .set(userData)  // Establecemos los datos en ese documento
            .addOnSuccessListener {
                Log.d("sesion", "Te has registrado exitosamente")
                Toast.makeText(context, "Te has registrado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al agregar usuario", e)
                Toast.makeText(context, "Error al agregar usuario", Toast.LENGTH_SHORT).show()
            }
    }




}