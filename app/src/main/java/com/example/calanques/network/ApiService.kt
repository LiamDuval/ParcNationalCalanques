package com.example.calanques.network

import com.example.calanques.model.Activite
import com.example.calanques.model.ActivityType
import com.example.calanques.model.RegisterRequest
import com.example.calanques.model.UserResponse
import com.example.calanques.model.ReservationResponse
import com.example.calanques.network.ApiConfig.BASE_URL
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
//
data class LoginResponse(
    @SerializedName("token", alternate = ["access_token"])
    val token: String?,
    @SerializedName("message", alternate = ["detail"])
    val message: String?
)

interface ApiService {

    // On utilise FormUrlEncoded pour FastAPI / OAuth2
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("api/auth/signup")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    companion object {
        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }

    @GET("api/activities")
    suspend fun getActivites(
        @Query("type_id") type: Int? = null
    ): Response<List<Activite>>

    @GET("api/activities/{id}")
    suspend fun getActivite(@Path("id") id: Int): Response<Activite>

    @GET("api/auth/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<UserResponse>

    @GET("api/reservations/mes")
    suspend fun getMesReservations(
        @Header("Authorization") token: String
    ): Response<List<ReservationResponse>>

    @GET("api/activity-types")
    suspend fun getActivityTypes(): Response<List<ActivityType>>
}