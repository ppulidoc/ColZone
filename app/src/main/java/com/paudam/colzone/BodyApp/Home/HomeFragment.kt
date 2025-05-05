package com.paudam.colzone.BodyApp.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    ): View {
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

        // Obtener publicaciones
        obtenerPublicaciones()

        return binding.root
    }

    private fun obtenerPublicaciones() {
        val userActualId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        Log.d("HomeFragment", "UID actual: $userActualId")

        db.collection("Users").document(userActualId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val seguidos = document.get("seguidos") as? List<String> ?: emptyList()
                    Log.d("HomeFragment", "Seguidos: $seguidos")

                    if (seguidos.isEmpty()) {
                        publiList.clear()
                        publiAdapter.notifyDataSetChanged()
                        return@addOnSuccessListener
                    }

                    db.collection("publicaciones")
                        .whereIn("userId", seguidos)
                        .orderBy("date", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                Log.d("HomeFragment", "No se encontraron publicaciones para los seguidos.")
                            } else {
                                Log.d("HomeFragment", "Se encontraron ${documents.size()} publicaciones.")
                            }

                            publiList.clear()
                            for (document in documents) {
                                val publicacion = Publicacion(
                                    publiId = document.id,
                                    userName = document.getString("userName") ?: "",
                                    userId = document.getString("userId") ?: "",
                                    title = document.getString("title") ?: "",
                                    rank = document.getLong("rank")?.toInt() ?: 0,
                                    favs = false,
                                    imageUrl = document.getString("imageUrl") ?: ""
                                )
                                Log.d("HomeFragment", "Publicación: $publicacion")
                                publiList.add(publicacion)
                            }
                            publiAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { e ->
                            Log.e("HomeFragment", "Error al obtener publicaciones filtradas: $e")
                        }
                } else {
                    Log.e("HomeFragment", "El documento del usuario actual no existe.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomeFragment", "Error al obtener seguidos del usuario actual: $e")
            }
    }
}
