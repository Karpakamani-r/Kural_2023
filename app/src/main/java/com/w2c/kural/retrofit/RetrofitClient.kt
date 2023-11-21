package com.w2c.kural.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient private constructor() {

    private val mRetrofitInstance: Retrofit by lazy {
        val okHttpClient = OkHttpClient()
            .newBuilder()
            .connectTimeout(4L, TimeUnit.SECONDS)
            .readTimeout(4L, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    fun getKuralApi(): KuralApi {
        return mRetrofitInstance.create(KuralApi::class.java)
    }

    companion object {
        private lateinit var INSTANCE: RetrofitClient
        private const val BASE_URL =
            "https://firebasestorage.googleapis.com/v0/b/valid-complex-136508.appspot.com/o/"

        @get:Synchronized
        val instance: RetrofitClient
            get() {
                if (!Companion::INSTANCE.isInitialized) {
                    INSTANCE = RetrofitClient()
                }
                return INSTANCE
            }
    }
}