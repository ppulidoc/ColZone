package com.paudam.colzone.BodyApp

import com.google.firebase.Timestamp
import java.util.Date

data class Publicacion(
    val publiId: String = "",
    val userName: String,
    val title: String,
    val rank: Int,
    val commentsId: Timestamp,
    val date: Timestamp = Timestamp.now()
)
