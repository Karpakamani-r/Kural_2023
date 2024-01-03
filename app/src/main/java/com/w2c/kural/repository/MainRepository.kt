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
    private val kuralLiveData = MutableLiveData<Kural>()

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

    suspend fun getKuralsByRange(
        context: Context,
        startIndex: Int,
        endIndex: Int
    ): LiveData<List<Kural>> {
        withContext(Dispatchers.IO) {
            var kuralList = localDataSource.getKuralsByRange(context, startIndex, endIndex)
            if (kuralList.isEmpty()) {
                val kurals = fetchKural(context)
                localDataSource.clearKural(context)
                localDataSource.cacheKural(context, kurals)
                kuralList = localDataSource.getKuralsByRange(context, startIndex, endIndex)
            }
            kuralListLiveData.postValue(kuralList)
        }
        return kuralListLiveData
    }

    suspend fun getKuralDetail(context: Context, number: Int): LiveData<Kural> {
        withContext(Dispatchers.IO) {
            localDataSource.getKural(context = context, number = number)?.let {
                kuralLiveData.postValue(it)
            }
        }
        return kuralLiveData
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
                for (j in 0..it.length() - 1) {
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
        for (i in 0..paalArray.length() - 1) {
            val p: JSONObject = paalArray.getJSONObject(i)
            if (paal.equals(p.getString("name"))) {
                val chapterGroup: JSONObject = p.getJSONObject("chapterGroup")
                chaptersGroupDetails = chapterGroup.getJSONArray("detail")
                break
            }
        }
        return chaptersGroupDetails
    }

    suspend fun getAthikaramByPaal(
        context: Context,
        paal: String,
        iyalName: String?
    ): List<String> {
        return withContext(Dispatchers.IO) {
            var chaptersGroupDetails: JSONArray? = getChapterGroupDetails(context, paal)
            val athikaram = mutableListOf<String>()
            chaptersGroupDetails?.let {
                for (j in 0..it.length() - 1) {
                    val iyal = it.getJSONObject(j)
                    val chapters = iyal.getJSONObject("chapters")
                    val name = iyal.getString("name")
                    val detail = chapters.getJSONArray("detail")
                    if (iyalName != null) {
                        if (iyalName.equals(name)) {
                            for (k in 0..detail.length() - 1) {
                                val n = detail.getJSONObject(k).getString("name").toString()
                                athikaram.add(n)
                            }
                            break
                        }
                    } else {
                        for (k in 0..detail.length() - 1) {
                            val nm = detail.getJSONObject(k).getString("name").toString()
                            athikaram.add(nm)
                        }
                    }
                }
            }
            athikaram
        }
    }

    suspend fun getKuralRangeByPaal(
        context: Context,
        paal: String,
        athikaram: String? = null
    ): String? {
        return withContext(Dispatchers.IO) {
            val chaptersGroupDetails: JSONArray? = getChapterGroupDetails(context, paal)
            return@withContext chaptersGroupDetails?.let {
                return@withContext if (athikaram != null) {
                    filterKuralByAthikaram(
                        athikaram = athikaram,
                        chaptersGroupDetails = it
                    )
                } else {
                    filterKuralByRange(it)
                }
            }
        }
    }

    suspend fun getFavorites(context: Context): LiveData<List<Kural>> {
        withContext(Dispatchers.IO) {
            val favourites = localDataSource.getFavourites(context)
            kuralListLiveData.postValue(favourites)
        }
        return kuralListLiveData
    }

    suspend fun updateFavorite(context: Context, kural: Kural): Int {
        return withContext(Dispatchers.IO) {
            val statusCode = localDataSource.updateFavorite(context, kural)
            if (statusCode > 0) {
                getFavorites(context)
            }
            statusCode
        }
    }

    private fun filterKuralByAthikaram(
        athikaram: String?,
        chaptersGroupDetails: JSONArray
    ): String {
        for (i in 0..chaptersGroupDetails.length() - 1) {
            val iyalObj = chaptersGroupDetails.getJSONObject(i)
            val chapters = iyalObj.getJSONObject("chapters")
            val chapterDetail = chapters.getJSONArray("detail")
            for (j in 0..chapterDetail.length() - 1) {
                val athikaramName = chapterDetail.getJSONObject(j).getString("name")
                if (athikaramName.equals(athikaram)) {
                    val start = chapterDetail.getJSONObject(j).getInt("start")
                    val end = chapterDetail.getJSONObject(j).getInt("end")
                    return "$start-$end"
                }
            }
        }
        return ""
    }

    private fun filterKuralByRange(chaptersGroupDetails: JSONArray): String {
        val chapterStart = chaptersGroupDetails.getJSONObject(0).getJSONObject("chapters")
        val chapterArrayStart = chapterStart.getJSONArray("detail")
        val startIndex = chapterArrayStart.getJSONObject(0).getInt("start")
        val chaptersEnd =
            chaptersGroupDetails.getJSONObject(chaptersGroupDetails.length().minus(1))
                .getJSONObject("chapters")
        val chapterArrayEnd = chaptersEnd.getJSONArray("detail")
        val endIndex =
            chapterArrayEnd.getJSONObject(chapterArrayEnd.length() - 1).getInt("end")
        return "$startIndex-$endIndex"
    }
}