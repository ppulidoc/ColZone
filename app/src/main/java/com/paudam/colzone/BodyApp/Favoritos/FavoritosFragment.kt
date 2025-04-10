package com.paudam.colzone.BodyApp.Favoritos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paudam.colzone.BodyApp.Publicacion
import com.paudam.colzone.BodyApp.SharedVM
import com.paudam.colzone.R
import com.paudam.colzone.adapter.PublicacionFavsAdapter
import com.paudam.colzone.databinding.FragmentFavoritosBinding

class FavoritosFragment : Fragment() {
    private lateinit var binding: FragmentFavoritosBinding
    private val sharedViewModel: SharedVM by activityViewModels()
    private lateinit var db: FirebaseFirestore
    private lateinit var publiFavsAdapter: PublicacionFavsAdapter
    private var publiFavsList = mutableListOf<Publicacion>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_favoritos, container, false
        )

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Consultar publicaciones favoritas del usuario
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            // Obtener las publicaciones favoritas del usuario desde Firestore
            val userDocRef = db.collection("Users").document(user.uid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val favoritos = document.get("favoritos") as? List<String> ?: emptyList()

                    if (favoritos.isNotEmpty()) {
                        // Obtener las publicaciones correspondientes a los favoritos
                        db.collection("publicaciones")
                            .whereIn("publiId", favoritos)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                publiFavsList.clear()
                                for (document in querySnapshot) {
                                    val publicacion = document.toObject(Publicacion::class.java)
                                    publiFavsList.add(publicacion)
                                }
                                // Notificar al adaptador que los datos han cambiado
                                publiFavsAdapter.notifyDataSetChanged()
                            }
                            .addOnFailureListener { e ->
                                // Manejo de error al obtener las publicaciones
                                e.printStackTrace()
                            }
                    }
                }
            }
        }

        // Configurar RecyclerView
        publiFavsAdapter = PublicacionFavsAdapter(publiFavsList) { publi ->
            // Acción al hacer clic en una publicación
        }
        binding.recyclerViewPublisFavs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = publiFavsAdapter
        }

        return binding.root
    }
}
