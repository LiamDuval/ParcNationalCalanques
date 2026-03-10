package com.example.calanques.network

import com.example.calanques.model.Activite
import com.example.calanques.model.LoginRequest
import com.example.calanques.model.LoginResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("activites")
    suspend fun getActivites(): Response<List<Activite>>

    @GET("activites/{id}")
    suspend fun getActivite(@Path("id") id: Int): Response<Activite>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("reservations/mes")
    suspend fun getMesReservations(
        @Header("Authorization") token: String
    ): Response<List<Any>>
}