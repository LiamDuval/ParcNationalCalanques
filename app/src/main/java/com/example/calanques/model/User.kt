package com.example.calanques.model

import com.google.gson.annotations.SerializedName
//
data class RegisterRequest(
    @SerializedName("username") val username: String, // E-mail (login)
    @SerializedName("password") val password: String, // Minuscule obligatoire
    @SerializedName("email") val email: String,
    @SerializedName("nom") val nom: String,
    @SerializedName("prenom") val prenom: String,
    @SerializedName("adresse") val adresse: String,
    @SerializedName("cp") val cp: String,
    @SerializedName("ville") val ville: String,
    @SerializedName("telephone") val telephone: String,
    @SerializedName("role_id") val roleId: Int = 2,
    
    // Champs optionnels ou avec noms adaptés
    @SerializedName("gender") val gender: Int,
    @SerializedName("birthdate") val birthDate: String,
    @SerializedName("family_count") val familyCount: Int,
    @SerializedName("full_name") val fullName: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val token: String?,
    val message: String?
)

data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val nom: String,
    val prenom: String,
    val adresse: String?,
    val cp: String?,
    val ville: String?,
    val telephone: String?
)

data class ReservationResponse(
    val id: Int,
    @SerializedName("activite_id") val activiteId: Int,
    @SerializedName("date_reservation") val dateReservation: String,
    val status: String,
    val activite: Activite?
)
