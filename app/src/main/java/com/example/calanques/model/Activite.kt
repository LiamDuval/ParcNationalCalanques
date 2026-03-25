package com.example.calanques.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Activite(
    val id: Int,
    val nom: String,
    val description: String?,
    val tarif: Double?,
    val duree: String?,
    val image_url: String?,
    @SerializedName("type_id") val type: Int
)