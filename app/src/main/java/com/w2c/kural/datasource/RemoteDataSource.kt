package com.w2c.kural.datasource

import android.content.Context
import com.w2c.kural.database.Kural
import com.w2c.kural.retrofit.RetrofitClient

class RemoteDataSource :
    DataSource {

    override suspend fun getKuralList(context: Context): List<Kural> {
        try {
            return RetrofitClient.instance.getKuralApi().getKural().kural
        } catch (e: Exception) {
            throw Exception(e)
        }
        return emptyList()
    }

    override suspend fun getFavourites(context: Context): List<Kural> {
        //Since we don't have API to maintain favourite in server,
        //we are leaving this function as Empty.
        return emptyList()
    }

    override suspend fun getKural(context: Context, number: Int): Kural? {
        //Since we don't have API to get kural from server,
        //we are leaving this function as Empty.
        return null
    }

    override suspend fun cacheKural(context: Context, kuralList: List<Kural>) {
        //Since we don't have API to get kural from server,
        //we are leaving this function as Empty.
    }
}