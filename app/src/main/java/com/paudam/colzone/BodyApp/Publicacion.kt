package com.paudam.colzone.BodyApp

import com.google.firebase.Timestamp

data class Publicacion(
    val publiId: String = "",
    val userName: String = "",
    val userId: String = "",
    val title: String = "",
    val rank: Int = 0,
    var favs: Boolean = false,
    val commentsId: Timestamp? = null,
    val date: Timestamp = Timestamp.now(),
    val imageUrl: String = "default_example.webp"
)
