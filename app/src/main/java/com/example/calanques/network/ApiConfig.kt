package com.example.calanques.network

import com.example.calanques.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.UnknownHostException

object ApiConfig {

    const val localUrl = "http://webngo.sio.bts:8004/"
    // On passe en http car le port 8001 ne supporte visiblement pas le HTTPS (TLS)
    const val prodUrl = "http://webngo.inforostand14.net:8001/"
    
    val BASE_URL = if (BuildConfig.DEBUG) localUrl else prodUrl

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            try {
                // Tentative 1 : URL locale
                chain.proceed(request)
            } catch (e: Exception) {
                // Si l'erreur est liée au DNS ou à la connexion
                if (e is UnknownHostException || e is IOException) {
                    if (request.url.host == "webngo.sio.bts") {
                        val fallbackUrl = request.url.newBuilder()
                            .scheme("http") // On force http ici aussi
                            .host("webngo.inforostand14.net")
                            .port(8001)
                            .build()
                        val newRequest = request.newBuilder().url(fallbackUrl).build()
                        return@addInterceptor chain.proceed(newRequest)
                    }
                }
                throw e
            }
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    object CalanquesApi {
        val retrofitService: ApiService by lazy {
            retrofit.create(ApiService::class.java)
        }
    }
}
