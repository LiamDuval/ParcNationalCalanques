package com.example.calanques.network

import com.example.calanques.model.Activite
import com.example.calanques.model.ActivityType
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/activites")
    suspend fun getActivites(): Response<List<Activite>>

    @GET("activites/{id}")
    suspend fun getActivite(@Path("id") id: Int): Response<Activite>

    @GET("reservations/mes")
    suspend fun getMesReservations(
        @Header("Authorization") token: String
    ): Response<List<Any>>

    @GET("api/activity-types")
    suspend fun getActivityTypes(): List<ActivityType>
}