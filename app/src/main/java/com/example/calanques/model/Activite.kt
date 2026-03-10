package com.example.calanques.model

data class Activite(
    val id: Int,
    val nom: String,
    val description: String?,
    val tarif: Double,
    val duree: String,
    val image_url: String?,
    val type: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: UserInfo
)

data class UserInfo(
    val id: Int,
    val nom: String,
    val prenom: String,
    val email: String,
    val role: String
)