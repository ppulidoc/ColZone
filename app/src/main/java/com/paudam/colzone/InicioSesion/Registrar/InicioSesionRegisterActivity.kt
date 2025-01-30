package com.paudam.colzone.InicioSesion.Registrar

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.paudam.colzone.BodyApp.BodyApp
import com.paudam.colzone.R
import com.paudam.colzone.databinding.ActivityInicioSesionRegisterBinding

class InicioSesionRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelLogin: InicioSesionRegisterVM by viewModels()
        val binding: ActivityInicioSesionRegisterBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_inicio_sesion_register
        )
        binding.buttonEnterApp.setOnClickListener(){
            //Comprovaciones en bdd para entrar
            val GoApp = Intent(this, BodyApp::class.java)
            startActivity(GoApp)
        }
    }
}