package com.w2c.kural.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface KuralApi {

    @GET(KURAL_URL)
    suspend fun getKural(
        @Query("alt") alt: String = "media",
        @Query("token") token: String = "9c8ad44f-a816-427f-8b25-21aabbefa9ae"
    ): KuralResponse

    companion object {
        const val KURAL_URL = "thirukkural.json"
    }
}