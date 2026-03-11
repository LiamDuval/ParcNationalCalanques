package com.example.calanques.model

import com.google.gson.annotations.SerializedName


data class ActivityType(
    @SerializedName("id") val id: Int,
    @SerializedName("libelle") val libelle: String,
    @SerializedName("image_url") val image_url: String?
)
