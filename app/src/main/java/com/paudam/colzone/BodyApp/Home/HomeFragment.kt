package com.paudam.colzone.BodyApp.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.paudam.colzone.BodyApp.Publicacion
import com.paudam.colzone.BodyApp.SharedVM
import com.paudam.colzone.R
import com.paudam.colzone.adapter.PublicacionAdapter
import com.paudam.colzone.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val sharedViewModel: SharedVM by activityViewModels()
    private lateinit var db: FirebaseFirestore
    private lateinit var publiAdapter: PublicacionAdapter
    private var publiList = mutableListOf<Publicacion>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home, container, false
        )

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Configurar RecyclerView
        publiAdapter = PublicacionAdapter(publiList) { publi ->
            // Acción al hacer clic en una publicación
        }
        binding.recyclerViewPublicaciones.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = publiAdapter
        }

        // Llamar a la función para obtener publicaciones
        obtenerPublicaciones()

        return binding.root
    }

    private fun obtenerPublicaciones() {
        db.collection("publicaciones")
            .orderBy("date") // Ordenar por fecha (opcional)
            .get()
            .addOnSuccessListener { documents ->
                publiList.clear()
                for (document in documents) {
                    val publicacion = document.toObject(Publicacion::class.java)
                    publiList.add(publicacion)
                }
                publiAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Manejar error
                println("Error al obtener publicaciones: $e")
            }
    }
}