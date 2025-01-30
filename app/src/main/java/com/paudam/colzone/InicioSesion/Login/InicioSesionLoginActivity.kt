package com.paudam.colzone.InicioSesion.Login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import com.paudam.colzone.BodyApp.BodyApp
import com.paudam.colzone.InicioSesion.Registrar.InicioSesionRegisterActivity
import com.paudam.colzone.R
import com.paudam.colzone.databinding.ActivityInicioSesionLoginBinding

class InicioSesionLoginActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelLogin: InicioSesionLoginVM by viewModels()
        val binding: ActivityInicioSesionLoginBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_inicio_sesion_login
        )

        binding.buttonEnterApp.setOnClickListener(){
            //Aqui ira comprovacion bdd
            val enter = true
            if (enter) {
                val GoApp = Intent(this, BodyApp::class.java)
                startActivity(GoApp)
            }
        }

        binding.buttonRegister.setOnClickListener(){
            val GoRegister = Intent(this, InicioSesionRegisterActivity::class.java)
            startActivity(GoRegister)
        }







    }
}