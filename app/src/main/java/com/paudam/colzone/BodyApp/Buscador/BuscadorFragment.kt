package com.paudam.colzone.BodyApp.Buscador

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paudam.colzone.BodyApp.SharedVM
import com.paudam.colzone.BodyApp.User
import com.paudam.colzone.R
import com.paudam.colzone.adapter.UsersAdapter
import com.paudam.colzone.databinding.FragmentBuscadorBinding

class BuscadorFragment : Fragment() {

    private lateinit var binding: FragmentBuscadorBinding
    private val sharedViewModel: SharedVM by activityViewModels()
    private lateinit var db: FirebaseFirestore
    private lateinit var usersAdapter: UsersAdapter
    private var userList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_buscador, container, false)
        db = FirebaseFirestore.getInstance()

        // Inicializar el adapter
        usersAdapter = UsersAdapter(userList) { user ->
            // Aquí podrías pasar a detalles o lo que quieras hacer al click
        }

        // Configurar RecyclerView
        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersAdapter
        }

        // Cargar usuarios desde Firestore
        loadUsersFromFirestore()

        return binding.root
    }

    private fun loadUsersFromFirestore() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                for (document in result) {
                    val uid = document.id
                    // Evitar mostrar al usuario actual
                    if (uid != currentUserUid) {
                        val name = document.getString("name") ?: ""
                        val email = document.getString("email") ?: ""
                        val imageUrl = document.getString("imageUrl") ?: "user_icon_free.png"

                        val user = User(
                            userId = uid,
                            name = name,
                            email = email,
                            imageUrl = imageUrl
                        )
                        userList.add(user)
                    }
                }
                usersAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // Log o mensaje de error si quieres
            }
    }
}
