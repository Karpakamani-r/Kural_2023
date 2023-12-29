package com.w2c.kural.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.w2c.kural.utils.TO_SEC
import com.w2c.kural.utils.BASE_URL

class RetrofitClient private constructor() {

    private val mRetrofitInstance: Retrofit by lazy {
        val okHttpClient = OkHttpClient()
            .newBuilder()
            .connectTimeout(TO_SEC, TimeUnit.SECONDS)
            .readTimeout(TO_SEC, TimeUnit.SECONDS)
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