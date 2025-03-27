package com.paudam.colzone.BodyApp.Perfil.CrearPubli

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.paudam.colzone.BodyApp.Perfil.PerfilGeneralVM
import com.paudam.colzone.BodyApp.SharedVM
import com.paudam.colzone.R
import com.paudam.colzone.databinding.FragmentCrearPublicacionBinding

class CrearPublicacionFragment : Fragment() {

    private lateinit var binding: FragmentCrearPublicacionBinding
    private val sharedViewModel: SharedVM by activityViewModels()
    private lateinit var db: FirebaseFirestore
    private lateinit var crearPublicacionVM: CrearPublicacionVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_crear_publicacion, container, false
        )
        crearPublicacionVM = ViewModelProvider(this).get(CrearPublicacionVM::class.java)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        binding.buttonConfirmar.setOnClickListener(){
            findNavController().navigate(R.id.action_crearPublicacionFragment_to_perfilGeneralFragment)
        }


        return binding.root
    }

}