package com.paudam.colzone.BodyApp.Perfil.CrearPubli

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.paudam.colzone.BodyApp.SharedVM
import com.paudam.colzone.R
import com.paudam.colzone.databinding.FragmentCrearPublicacionBinding
import com.google.firebase.Timestamp

class CrearPublicacionFragment : Fragment() {

    private lateinit var binding: FragmentCrearPublicacionBinding
    private val sharedViewModel: SharedVM by activityViewModels()
    private lateinit var db: FirebaseFirestore
    private lateinit var crearPublicacionVM: CrearPublicacionVM

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var storageRef: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_crear_publicacion, container, false
        )
        crearPublicacionVM = ViewModelProvider(this).get(CrearPublicacionVM::class.java)
        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        var userActual = sharedViewModel.userActual.toString()
        var userName = ""

        sharedViewModel.obtenirUsername(db, userActual) { name ->
            userName = name.toString()
        }

        binding.addPubliButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST)
        }

        binding.buttonConfirmar.setOnClickListener {
            val textTitle = binding.editTextTextMultiLine.text.toString().trim()
            val rank = 5
            val commentsId = Timestamp.now()
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val publiId = db.collection("publicaciones").document().id

            if (imageUri != null) {
                val imageRef = storageRef.child("imagenes_publicaciones/$publiId.jpg")
                imageRef.putFile(imageUri!!)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()

                            crearPublicacionVM.insertPublicacion(
                                db,
                                userName,
                                userId.toString(),
                                textTitle,
                                rank,
                                commentsId,
                                imageUrl,
                                publiId,
                                requireContext()
                            )

                            findNavController().navigate(R.id.action_crearPublicacionFragment_to_perfilGeneralFragment)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Selecciona una imagen", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.addPubliButton.setImageURI(imageUri)
        }
    }
}
