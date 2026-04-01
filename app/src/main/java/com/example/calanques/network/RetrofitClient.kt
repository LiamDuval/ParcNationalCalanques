package com.example.calanques.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(ApiConfig.okHttpClient) // AJOUTER CECI pour le fallback automatique
            .build()
            .create(ApiService::class.java)
    }
}