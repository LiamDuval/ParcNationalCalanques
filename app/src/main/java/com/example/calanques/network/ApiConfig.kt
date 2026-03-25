package com.example.calanques.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.UnknownHostException

object ApiConfig {

    const val localUrl = "http://webngo.sio.bts:8004/"

    val BASE_URL = localUrl
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            try {
                chain.proceed(request)
            } catch (e: Exception) {
                if (e is UnknownHostException || e is IOException) {
                    if (request.url.host == "webngo.sio.bts") {
                        val fallbackUrl = request.url.newBuilder()
                            .scheme("http")
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
