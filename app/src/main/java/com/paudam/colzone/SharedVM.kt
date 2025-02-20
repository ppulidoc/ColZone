package com.paudam.colzone

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class SharedVM: ViewModel() {
    private var _userActual: String?=null
    val userActual: String?
        get()=_userActual

    //Obtener user actual
    fun userActual(correo: String) {
        _userActual = correo
    }
}