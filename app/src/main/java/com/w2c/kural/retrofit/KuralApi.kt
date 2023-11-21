package com.w2c.kural.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface KuralApi {

    @GET(KURAL_URL)
    suspend fun getKural(
        @Query("alt") alt: String = "media",
        @Query("token") token: String = "95f05837-32a9-484c-a584-e328ec4f97d4"
    ): KuralResponse

    companion object {
        const val KURAL_URL = "thirukkural.json"
    }
}