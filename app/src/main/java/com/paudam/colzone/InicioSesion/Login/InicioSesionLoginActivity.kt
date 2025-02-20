package com.paudam.colzone.InicioSesion.Login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.paudam.colzone.BodyApp.BodyApp
import com.paudam.colzone.BodyApp.ProviderType
import com.paudam.colzone.InicioSesion.Registrar.InicioSesionRegisterActivity
import com.paudam.colzone.R
import com.paudam.colzone.BodyApp.SharedVM
import com.paudam.colzone.databinding.ActivityInicioSesionLoginBinding

class InicioSesionLoginActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private val viewModelLogin: InicioSesionLoginVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding: ActivityInicioSesionLoginBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_inicio_sesion_login
        )

        var userActual = "";
        binding.buttonEnterApp.setOnClickListener() {
            //Aqui ira comprovacion bdd
            if (binding.editTextEmail.text.isNotEmpty() && binding.editTextPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        binding.editTextEmail.text.toString(),
                        binding.editTextPassword.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            userActual = binding.editTextEmail.text.toString()
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC, userActual)
                        } else {
                            showAlert()
                        }
                    }
            }
        }

        binding.buttonRegister.setOnClickListener() {
            val GoRegister = Intent(this, InicioSesionRegisterActivity::class.java)
            startActivity(GoRegister)
        }

    }


    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Err")
        builder.setMessage("err autenticando user")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType, userActual: String) {
        val appIntent = Intent(this, BodyApp::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
            putExtra("userActual", userActual )
        }
        startActivity(appIntent)

    }
}