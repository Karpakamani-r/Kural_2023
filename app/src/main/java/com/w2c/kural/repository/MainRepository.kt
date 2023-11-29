package com.w2c.kural.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.w2c.kural.database.Kural
import com.w2c.kural.datasource.LocalDataSource
import com.w2c.kural.datasource.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class MainRepository constructor(
    private val localDataSource: LocalDataSource, private val remoteDataSource: RemoteDataSource
) {
    private val kuralListLiveData = MutableLiveData<List<Kural>>()

    suspend fun getKurals(context: Context): LiveData<List<Kural>> {
        withContext(Dispatchers.IO) {
            var kuralList = localDataSource.getKuralList(context)
            if (kuralList.isEmpty()) {
                kuralList = fetchKural(context)
                localDataSource.clearKural(context)
                localDataSource.cacheKural(context, kuralList)
            }
            kuralListLiveData.postValue(kuralList)
        }
        return kuralListLiveData
    }

    private suspend fun fetchKural(context: Context): List<Kural> {
        return try {
            remoteDataSource.getKuralList(context)
        } catch (e: Exception) {
            localDataSource.fetchKuralsFromAssets(context)
        }
    }

    suspend fun getPaals(context: Context): List<String> {
        val paalList = mutableListOf<String>()
        val paalArray = getBaseDetails(context)
        for (i in 0..paalArray.length() - 1) {
            val paal: JSONObject = paalArray.getJSONObject(i)
            paalList.add(paal.get("name").toString())
        }
        return paalList
    }

    suspend fun getBaseDetails(context: Context): JSONArray {
        val detailJson = localDataSource.loadPaalFromAssets(context)
        val detailsArray: JSONArray = JSONArray(detailJson)
        val detailsObj: JSONObject = detailsArray.getJSONObject(0)
        val paalObj: JSONObject = detailsObj.getJSONObject("section")
        return paalObj.getJSONArray("detail")
    }

    suspend fun getIyalByPaal(context: Context, paal: String): List<String> {
        return withContext(Dispatchers.IO) {
            var chaptersGroupDetails: JSONArray? = getChapterGroupDetails(context, paal)
            val iyals = mutableListOf<String>()
            chaptersGroupDetails?.let {
                for (j in 0..it.length()-1) {
                    val iyal = it.getJSONObject(j)
                    val iyalName = iyal.get("name").toString()
                    iyals.add(iyalName)
                }
            }
            iyals
        }
    }

    private suspend fun getChapterGroupDetails(context: Context, paal: String): JSONArray? {
        var chaptersGroupDetails: JSONArray? = null
        val paalArray = getBaseDetails(context)
        for (i in 0..paalArray.length()-1) {
            val p: JSONObject = paalArray.getJSONObject(i)
            if (paal.equals(p.getString("name"))) {
                val chapterGroup: JSONObject = p.getJSONObject("chapterGroup")
                chaptersGroupDetails = chapterGroup.getJSONArray("detail")
                break
            }
        }
        return chaptersGroupDetails
    }

    suspend fun getAthikaramByPaal(context: Context, paal: String): List<String> {
        return withContext(Dispatchers.IO) {
            var chaptersGroupDetails: JSONArray? = getChapterGroupDetails(context, paal)
            val athikaram = mutableListOf<String>()
            chaptersGroupDetails?.let {
                for (j in 0..it.length()-1) {
                    val iyal = it.getJSONObject(j)
                    val chapters = iyal.getJSONObject("chapters")
                    val detail = chapters.getJSONArray("detail")
                    for (k in 0..detail.length()-1) {
                        val name = iyal.get("name").toString()
                        athikaram.add(name)
                    }
                }
            }
            athikaram
        }
    }
}