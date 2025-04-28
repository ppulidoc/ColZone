package com.paudam.colzone.BodyApp.Perfil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paudam.colzone.BodyApp.SharedVM
import com.paudam.colzone.R
import com.paudam.colzone.databinding.FragmentPerfilGeneralBinding

class PerfilGeneralFragment : Fragment() {

    private lateinit var binding: FragmentPerfilGeneralBinding
    private val sharedViewModel: SharedVM by activityViewModels()
    private lateinit var db: FirebaseFirestore
    private lateinit var perfilGeneralVM: PerfilGeneralVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_perfil_general, container, false
        )
        perfilGeneralVM = ViewModelProvider(this).get(PerfilGeneralVM::class.java)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Agafar usuariactual
        var userActual = ""
        userActual = sharedViewModel.userActual.toString()

        //Obtenir dades user
        perfilGeneralVM.obtenirUsername(db, userActual) { name ->
            var userName = name
            binding.textViewUsername.text = name ?: "Usuario no encontrado"
        }
        val userActualId = FirebaseAuth.getInstance().currentUser?.uid
        if (userActualId != null) {
            perfilGeneralVM.contarFollows(db,userActualId){ numFollows ->
                var numFollows = numFollows
                binding.textViewNumFolows.text = numFollows.toString() ?: "0"
            }
        }

        binding.addPubliButton.setOnClickListener(){
            findNavController().navigate(R.id.action_perfilGeneralFragment_to_crearPublicacionFragment)
        }






        return binding.root
    }

}