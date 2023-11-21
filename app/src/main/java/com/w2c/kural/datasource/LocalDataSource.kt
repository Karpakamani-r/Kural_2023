package com.w2c.kural.datasource

import android.content.Context
import com.w2c.kural.database.DatabaseController
import com.w2c.kural.database.Kural
import com.w2c.kural.datasource.DataSource

class LocalDataSource : DataSource {

    override suspend fun getKuralList(context: Context): List<Kural> {
        return DatabaseController.getInstance(context)
            .kuralDAO
            .allKural
    }

    override suspend fun getFavourites(context: Context): List<Kural> {
        return DatabaseController.getInstance(context)
            .kuralDAO
            .favKuralList
    }

    override suspend fun getKural(context: Context, number: Int): Kural? {
        return DatabaseController.getInstance(context)
            .kuralDAO
            .getKural(number)
    }

    override suspend fun cacheKural(context: Context, kuralList: List<Kural>) {
        for (k in kuralList) {
            DatabaseController.getInstance(context)
                .kuralDAO
                .insertKuralData(k)
        }
    }

    fun clearKural(context: Context) {
        DatabaseController.getInstance(context)
            .kuralDAO
            .deleteData()
    }
}