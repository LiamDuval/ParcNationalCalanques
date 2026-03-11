package com.example.calanques.network

import com.example.calanques.model.ActivityType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object ApiConfig {
    const val BASE_URL = "http://webngo.sio.bts:8004/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    interface ApiService {
        @GET("api/activity-types")
        suspend fun getActivityTypes(): List<ActivityType>
    }

    object CalanquesApi {
        val retrofitService: ApiService by lazy {
            retrofit.create(ApiService::class.java)
        }
    }
}