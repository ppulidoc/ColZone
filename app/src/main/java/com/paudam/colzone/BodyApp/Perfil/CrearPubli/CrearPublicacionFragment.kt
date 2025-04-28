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
import com.google.firebase.auth.FirebaseAuth
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


        // ------------------------------------------------------
        // Agafar usuariactual
        var userActual = ""

        userActual = sharedViewModel.userActual.toString()

        var userName="";

        //Obtenir dades user
        sharedViewModel.obtenirUsername(db, userActual) { name ->
            userName = name.toString()
        }
        //----------------------------------------------------

        binding.buttonConfirmar.setOnClickListener(){
            // Obtener valores del formulario

            val textTitle = binding.editTextTextMultiLine.text.toString().trim()
            val rank = 5 // Puedes cambiar esto para obtenerlo dinámicamente

            // Generar timestamp actual para commentsId
            val commentsId = com.google.firebase.Timestamp.now()
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            // Llamar a la función insertPublicacion con los parámetros adecuados
            crearPublicacionVM.insertPublicacion(db, userName,userId.toString(), textTitle, rank, commentsId, requireContext())

            // Navegar a la pantalla de perfil después de guardar la publicación
            findNavController().navigate(R.id.action_crearPublicacionFragment_to_perfilGeneralFragment)
        }


        return binding.root
    }

}