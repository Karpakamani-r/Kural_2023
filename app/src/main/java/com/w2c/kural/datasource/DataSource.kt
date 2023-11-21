package com.w2c.kural.datasource

import android.content.Context
import com.w2c.kural.database.Kural

interface DataSource {
    suspend fun getKuralList(context: Context): List<Kural>
    suspend fun getFavourites(context: Context): List<Kural>
    suspend fun getKural(context: Context, number: Int): Kural?
    suspend fun cacheKural(context: Context, kuralList: List<Kural>)
}