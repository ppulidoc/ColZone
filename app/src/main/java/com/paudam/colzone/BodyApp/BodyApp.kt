package com.paudam.colzone.BodyApp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.paudam.colzone.R
import com.paudam.colzone.databinding.ActivityBodyAppBinding

enum class ProviderType{
    BASIC
}

class BodyApp : AppCompatActivity() {
    private lateinit var navController: NavController
    private val sharedViewModel: SharedVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityBodyAppBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_body_app
        )

        val userActual = intent.getStringExtra("userActual") ?: ""
        println("Usuario Actual: $userActual")
        sharedViewModel.userActual(userActual)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        setupWithNavController(binding.bottomNavigationView, navController)
    }
}