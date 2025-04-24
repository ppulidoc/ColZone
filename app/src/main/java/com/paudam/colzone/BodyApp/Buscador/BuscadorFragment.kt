package com.paudam.colzone.BodyApp.Buscador

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
import com.paudam.colzone.BodyApp.SharedVM
import com.paudam.colzone.BodyApp.User
import com.paudam.colzone.R
import com.paudam.colzone.adapter.UsersAdapter
import com.paudam.colzone.adapter.UsersAdapterSugerencia
import com.paudam.colzone.databinding.FragmentBuscadorBinding

class BuscadorFragment : Fragment() {

    private lateinit var binding: FragmentBuscadorBinding
    private val sharedViewModel: SharedVM by activityViewModels()
    private lateinit var db: FirebaseFirestore
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var usersAdapterSugerencia: UsersAdapterSugerencia

    private var userList = mutableListOf<User>()
    private var userListSuggest = mutableListOf<User>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_buscador, container, false)
        db = FirebaseFirestore.getInstance()

        usersAdapter = UsersAdapter(userList) { user ->
            // Acción al hacer clic en un usuario (si quieres abrir perfil, etc.)
        }

        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersAdapter
        }


        usersAdapterSugerencia = UsersAdapterSugerencia(userListSuggest) { user ->
            // Acción al hacer clic en un usuario (si quieres abrir perfil, etc.)
        }

        binding.recyclerViewUsersGlobal.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersAdapterSugerencia
        }

        loadUsersFromFirestoreSuggest()





        binding.searchText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    loadUsersFromFirestore(query)
                } else {
                    userList.clear()
                    usersAdapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        return binding.root
    }

    private fun loadUsersFromFirestore(filter: String) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                for (document in result) {
                    val uid = document.id
                    if (uid != currentUserUid) {
                        val name = document.getString("name") ?: ""
                        val email = document.getString("email") ?: ""
                        val imageUrl = document.getString("imageUrl") ?: "user_icon_free.png"

                        if (name.contains(filter, ignoreCase = true)) {
                            val user = User(
                                userId = uid,
                                name = name,
                                email = email,
                                imageUrl = imageUrl
                            )
                            userList.add(user)
                        }
                    }
                }
                usersAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al buscar usuarios", it)
            }
    }

    private fun loadUsersFromFirestoreSuggest() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                val allUsers = mutableListOf<User>()

                for (document in result) {
                    val uid = document.id
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
                        allUsers.add(user)
                    }
                }

                // Elegir 2 usuarios aleatorios
                userListSuggest.clear()
                userListSuggest.addAll(allUsers.shuffled().take(2))

                usersAdapterSugerencia.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al buscar usuarios", it)
            }
    }
}