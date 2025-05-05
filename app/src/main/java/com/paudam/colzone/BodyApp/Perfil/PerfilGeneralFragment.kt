package com.paudam.colzone.BodyApp.Perfil

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.paudam.colzone.BodyApp.Publicacion
import com.paudam.colzone.BodyApp.SharedVM
import com.paudam.colzone.InicioSesion.Login.InicioSesionLoginActivity
import com.paudam.colzone.R
import com.paudam.colzone.adapter.PublicacionPerfilAdapter
import com.paudam.colzone.databinding.FragmentPerfilGeneralBinding

class PerfilGeneralFragment : Fragment() {

    private lateinit var binding: FragmentPerfilGeneralBinding
    private val sharedViewModel: SharedVM by activityViewModels()
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var userActualId: String? = null
    private lateinit var adapter: PublicacionPerfilAdapter

    private lateinit var perfilGeneralVM: PerfilGeneralVM  // ViewModel

    // Selector de imagen
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageUri = result.data!!.data
            imageUri?.let { uploadImageToFirebase(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_perfil_general, container, false
        )

        // Inicialización de Firestore, FirebaseStorage y ViewModel
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        perfilGeneralVM = ViewModelProvider(this).get(PerfilGeneralVM::class.java)

        // Obtención del userActualId
        val userActual = sharedViewModel.userActual.toString()
        userActualId = FirebaseAuth.getInstance().currentUser?.uid

        // Obtener nombre de usuario desde el ViewModel
        perfilGeneralVM.obtenirUsername(db, userActual) { name ->
            binding.textViewUsername.text = name ?: "Usuario no encontrado"
        }

        // Contador de follows
        userActualId?.let { uid ->
            perfilGeneralVM.contarFollows(db, uid) { numFollows ->
                binding.textViewNumFolows.text = numFollows.toString()
            }
        }

        binding.imageButtonLogOut.setOnClickListener {
            requireActivity().finish()
            val intent = Intent(requireContext(), InicioSesionLoginActivity::class.java)
            startActivity(intent)
        }

        // Navegar a crear publicación
        binding.addPubliButton.setOnClickListener {
            findNavController().navigate(R.id.action_perfilGeneralFragment_to_crearPublicacionFragment)
        }

        // Pulsar imagen de perfil para cambiarla
        binding.imageViewProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // Cargar imagen de perfil al inicio
        loadProfileImage()

        // Asignar el LayoutManager con GridLayoutManager (2 columnas)
        binding.recyclerViewPublicacionesPerfil.layoutManager = GridLayoutManager(requireContext(), 2)

        // Obtener las publicaciones del usuario actual
        userActualId?.let { uid ->
            db.collection("publicaciones")
                .whereEqualTo("userId", uid)  // Filtramos por el ID del usuario actual
                .get()
                .addOnSuccessListener { result ->
                    val publicaciones = mutableListOf<Publicacion>()
                    for (document in result) {
                        val publicacion = document.toObject(Publicacion::class.java)
                        publicaciones.add(publicacion)
                    }

                    // Pasar directamente la lista de publicaciones al adaptador
                    adapter = PublicacionPerfilAdapter(publicaciones) { publicacion ->
                        showDeleteDialog(publicacion)
                    }
                    binding.recyclerViewPublicacionesPerfil.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    // Manejo de error si no se puede obtener las publicaciones
                }
        }

        return binding.root
    }

    // Función para mostrar el dialog de confirmación
    private fun showDeleteDialog(publicacion: Publicacion) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Publicación")
            .setMessage("¿Estás seguro de que deseas eliminar esta publicación?")
            .setPositiveButton("Eliminar") { dialog: DialogInterface, which: Int ->
                deletePublication(publicacion.publiId)
            }
            .setNegativeButton("Cancelar") { dialog: DialogInterface, which: Int ->
                dialog.dismiss()  // Cerrar el diálogo sin hacer nada
            }
            .create()

        alertDialog.show()
    }

    // Función para eliminar la publicación de Firestore
    private fun deletePublication(publicacionId: String) {
        db.collection("publicaciones").document(publicacionId)
            .delete()
            .addOnSuccessListener {
                // Actualizar la lista de publicaciones después de eliminar
                val updatedList = adapter.publicacionesList.filter { it.publiId != publicacionId }.toMutableList()
                adapter.updateList(updatedList)
            }
            .addOnFailureListener { exception ->
                // Manejo de error si no se puede eliminar
            }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val uid = userActualId ?: return
        val storageRef = storage.reference.child("profile_images/$uid.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    db.collection("Users").document(uid)
                        .update("profileImageUrl", uri.toString())
                        .addOnSuccessListener {
                            loadProfileImage()
                        }
                        .addOnFailureListener {
                            // Manejo de errores si no se puede guardar la URL
                        }
                }
            }
            .addOnFailureListener {
                // Manejo de errores si no se puede subir la imagen
            }
    }

    private fun loadProfileImage() {
        val uid = userActualId ?: return

        db.collection("Users").document(uid).get()
            .addOnSuccessListener { document ->
                val imageUrl = document.getString("profileImageUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .into(binding.imageViewProfile)
                } else {
                    Glide.with(requireContext())
                        .load(R.drawable.default_profile_image)
                        .into(binding.imageViewProfile)
                }
            }
            .addOnFailureListener {
                // En caso de error al recuperar la URL
            }
    }
}
