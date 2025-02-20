package com.paudam.colzone.InicioSesion.Registrar

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paudam.colzone.InicioSesion.Login.InicioSesionLoginActivity
import com.paudam.colzone.R
import com.paudam.colzone.databinding.ActivityInicioSesionRegisterBinding
class InicioSesionRegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val viewModelLogin: InicioSesionRegisterVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityInicioSesionRegisterBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_inicio_sesion_register
        )

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.buttonEnterApp.setOnClickListener {
            //coger campos necesarios para bdd y para comprovaciones
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextTextPassword.text.toString().trim()
            val passwrdConfirm = binding.editTextTextPasswordConfirm.text.toString().trim()
            val nombreUser = binding.editTextUser.text.toString().trim()
            val edadUser = binding.editTextAge.text.toString().trim()

            // comporvaciones
            if (email.isEmpty() || password.isEmpty() || nombreUser.isEmpty() || edadUser.isEmpty()) {
                viewModelLogin.showAlert("Todos los campos son obligatorios", this)
                return@setOnClickListener
            }

            val edad = edadUser.toIntOrNull()
            if (edad == null || edad < 0) {
                viewModelLogin.showAlert("La edad debe ser un número válido", this)
                return@setOnClickListener
            }

            if (password !== passwrdConfirm ) {
                viewModelLogin.showAlert("La contraseña tiene que ser igual", this)
                return@setOnClickListener
            }

            //crear el user en el auth
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //si el auth es correcto busca si existe el usuario
                    task.result?.user?.let { userActual ->
                        //si entra bien hace el insert
                        viewModelLogin.createUser(db, userActual, nombreUser, edad, this)
                        startActivity(Intent(this, InicioSesionLoginActivity::class.java))
                        finish()
                    } ?: run {
                        viewModelLogin.showAlert("Error: No se pudo obtener el usuario después del registro", this)
                    }
                } else {
                    viewModelLogin.showAlert("Error al registrar usuario: ${task.exception?.message}", this)
                }
            }
        }
    }
}

