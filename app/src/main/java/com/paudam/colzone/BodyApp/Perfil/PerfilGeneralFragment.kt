package com.paudam.colzone.BodyApp.Perfil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.paudam.colzone.BodyApp.SharedVM
import com.paudam.colzone.InicioSesion.Login.InicioSesionLoginActivity
import com.paudam.colzone.R
import com.paudam.colzone.databinding.FragmentPerfilGeneralBinding

class PerfilGeneralFragment : Fragment() {

    private lateinit var binding: FragmentPerfilGeneralBinding
    private val sharedViewModel: SharedVM by activityViewModels()
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var userActualId: String? = null

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

        // Obtención de userActualId
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
            // Cerrar la actividad actual (que es la actividad del perfil)
            requireActivity().finish()

            // Crear un Intent para iniciar la actividad de Inicio de Sesión
            val intent = Intent(requireContext(), InicioSesionLoginActivity::class.java) // Sustituye LoginActivity con el nombre de tu actividad de inicio de sesión
            startActivity(intent)  // Inicia la actividad de Inicio de Sesión
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

        return binding.root
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val uid = userActualId ?: return
        val storageRef = storage.reference.child("profile_images/$uid.jpg")  // Usamos el UID como nombre del archivo

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Guardamos la URL de la imagen en Firestore
                    db.collection("Users").document(uid)
                        .update("profileImageUrl", uri.toString())
                        .addOnSuccessListener {
                            // Después de guardar la URL, recargamos la imagen en el perfil
                            loadProfileImage()  // Carga la nueva imagen del perfil
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

        // Recuperar la URL de la imagen desde Firestore
        db.collection("Users").document(uid).get()
            .addOnSuccessListener { document ->
                val imageUrl = document.getString("profileImageUrl")  // Recuperamos la URL de la imagen

                if (!imageUrl.isNullOrEmpty()) {
                    // Si la URL no está vacía, cargamos la imagen desde Firebase Storage
                    Glide.with(requireContext())
                        .load(imageUrl)  // Carga la imagen desde la URL obtenida
                        .into(binding.imageViewProfile)
                } else {
                    // Si no hay imagen de perfil, mostramos una imagen por defecto
                    Glide.with(requireContext())
                        .load(R.drawable.default_profile_image)  // Imagen por defecto
                        .into(binding.imageViewProfile)
                }
            }
            .addOnFailureListener {
                // En caso de error al recuperar la URL
            }
    }
}
