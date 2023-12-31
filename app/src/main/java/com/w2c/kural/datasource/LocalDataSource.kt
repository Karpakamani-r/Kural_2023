package com.w2c.kural.datasource

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.w2c.kural.R
import com.w2c.kural.database.DatabaseController
import com.w2c.kural.database.Kural
import com.w2c.kural.datasource.DataSource
import com.w2c.kural.model.Setting
import com.w2c.kural.retrofit.KuralResponse
import java.io.FileNotFoundException
import java.util.ArrayList

class LocalDataSource : DataSource {

    override suspend fun getKuralList(context: Context): List<Kural> {
        return DatabaseController.getInstance(context).kuralDAO.allKural
    }

    override suspend fun getFavourites(context: Context): List<Kural> {
        return DatabaseController.getInstance(context).kuralDAO.favKuralList
    }

    override suspend fun getKural(context: Context, number: Int): Kural? {
        return DatabaseController.getInstance(context).kuralDAO.getKural(number)
    }

    override suspend fun cacheKural(context: Context, kuralList: List<Kural>) {
        for (k in kuralList) {
            DatabaseController.getInstance(context).kuralDAO.insertKuralData(k)
        }
    }

    fun clearKural(context: Context) {
        DatabaseController.getInstance(context).kuralDAO.deleteData()
    }

    suspend fun fetchKuralsFromAssets(context: Context): List<Kural> {
        try {
            val kuralJson = context.assets.open("thirukkural.json").reader().use {
                it.readText()
            }
            return Gson().fromJson<KuralResponse>(
                kuralJson,
                object : TypeToken<KuralResponse>() {}.type
            ).kural
        } catch (fnfException: FileNotFoundException) {
            return emptyList()
        }
    }

    suspend fun loadPaalFromAssets(context: Context): String {
        return context.assets.open("details.json").bufferedReader().use {
            it.readText()
        }
    }

    suspend fun getKuralsByRange(context: Context, startIndex: Int, endIndex: Int): List<Kural> {
        return DatabaseController.getInstance(context).kuralDAO.getKuralByRange(
            startIndex,
            endIndex
        )
    }

    suspend fun updateFavorite(context: Context, kural: Kural): Int {
        return DatabaseController.getInstance(context)
            .kuralDAO
            .updateKuralData(kural)
    }

    fun getSettingsData(context: Context): List<Setting> = buildList{
        val dailyNotify = Setting(
            context.getString(R.string.daily_one_kural),
            listOf(R.drawable.ic_bell_enable, R.drawable.ic_bell_disable)
        )
        add(dailyNotify)

        val feedBack = Setting(
            context.getString(R.string.feedback),
            listOf(R.drawable.ic_email)
        )
        add(feedBack)

        val rateUs = Setting(
            context.getString(R.string.rate_us),
            listOf(R.drawable.ic_rate)
        )
        add(rateUs)

        val shareApp = Setting(
            context.getString(R.string.share_app),
            listOf(R.drawable.ic_share)
        )
        add(shareApp)
    }
}