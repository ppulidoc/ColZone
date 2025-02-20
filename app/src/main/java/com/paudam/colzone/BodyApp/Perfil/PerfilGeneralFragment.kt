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
import com.paudam.colzone.BodyApp.SharedVM
import com.paudam.colzone.R
import com.paudam.colzone.databinding.FragmentPerfilGeneralBinding

class PerfilGeneralFragment : Fragment() {

    private lateinit var binding: FragmentPerfilGeneralBinding
    private val sharedViewModel: SharedVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_perfil_general, container, false
        )


        binding.textViewCorreo.text = sharedViewModel.userActual

        return binding.root
    }

}