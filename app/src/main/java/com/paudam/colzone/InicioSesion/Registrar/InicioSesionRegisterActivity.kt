package com.paudam.colzone.InicioSesion.Registrar

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.paudam.colzone.BodyApp.BodyApp
import com.paudam.colzone.BodyApp.ProviderType
import com.paudam.colzone.InicioSesion.Login.InicioSesionLoginActivity
import com.paudam.colzone.R
import com.paudam.colzone.databinding.ActivityInicioSesionRegisterBinding

class InicioSesionRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelLogin: InicioSesionRegisterVM by viewModels()
        val binding: ActivityInicioSesionRegisterBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_inicio_sesion_register
        )

        binding.buttonEnterApp.setOnClickListener() {
            if (binding.editTextEmail.text.isNotEmpty() && binding.editTextTextPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.editTextEmail.text.toString(),
                    binding.editTextTextPassword.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Cuando la creación del usuario sea exitosa, redirigir al Login
                        val GoLogin = Intent(this, InicioSesionLoginActivity::class.java)
                        startActivity(GoLogin)
                        finish() // Asegúrate de cerrar la actividad de registro
                    } else {
                        // Si ocurre un error, mostrar un mensaje
                        showAlert("Error al registrar el usuario")
                    }
                }
            } else {
                showAlert("Por favor, ingresa un correo y una contraseña válidos")
            }
        }
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
