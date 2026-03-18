package com.example.calanques.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable // INDISPENSABLE pour que Retrofit puisse remplir cette classe
data class Activite(
    @SerialName("id") val id: Int,
    @SerialName("nom") val nom: String,
    @SerialName("description") val description: String,
    @SerialName("tarif") val tarif: Double,
    @SerialName("duree") val duree: Double,
    @SerialName("image_url") val image_url: String,
    @SerialName("type") val type: Int
)