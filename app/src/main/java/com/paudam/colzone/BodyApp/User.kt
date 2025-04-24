package com.paudam.colzone.BodyApp

data class User(
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var seguidos: Boolean = false,
    var imageUrl: String = "user_icon_free.png",
)
