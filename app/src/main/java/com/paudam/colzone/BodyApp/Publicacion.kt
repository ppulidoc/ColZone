package com.paudam.colzone.BodyApp

import com.google.firebase.Timestamp

data class Publicacion(
    val publiId: String = "",
    val userName: String = "",
    val title: String = "",
    val rank: Int = 0,
    val commentsId: Timestamp? = null,
    val date: Timestamp = Timestamp.now(),
    val imageUrl: String = "https://via.placeholder.com/300" // URL de la imagen asociada
)
