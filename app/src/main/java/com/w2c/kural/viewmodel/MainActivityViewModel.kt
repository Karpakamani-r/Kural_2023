package com.w2c.kural.viewmodel

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.w2c.kural.R
import com.w2c.kural.database.DatabaseController
import com.w2c.kural.database.Kural
import com.w2c.kural.repository.MainRepository
import com.w2c.kural.utils.Menus
import com.w2c.kural.utils.ScreenTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.AbstractList

class MainActivityViewModel(private val mainRepository: MainRepository) :
    ViewModel() {

    private val kuralCache_: MutableList<Kural> = mutableListOf()
    val kuralCache: List<Kural> = kuralCache_
    private val data: MutableLiveData<List<String>> = MutableLiveData<List<String>>()
    fun cacheKural(list: List<Kural>) {
        kuralCache_.clear()
        kuralCache_.addAll(list)
    }

    suspend fun getKurals(
        context: Context,
        screenTypes: ScreenTypes? = null,
        paal: String? = null
    ): LiveData<List<Kural>> {
        return mainRepository.getKurals(context)
    }

    suspend fun getKuralsByRange(
        context: Context,
        paal: String,
        athikaram: String?
    ): LiveData<List<Kural>> {
        val kuralRange = mainRepository.getKuralRangeByPaal(context, paal, athikaram)
        val indexes = kuralRange?.split("-")
        var startIndex = 1
        var endIndex = 1330
        indexes?.let {
            startIndex = it[0].toInt()
            endIndex = it[1].toInt()
        }
        return mainRepository.getKuralsByRange(context, startIndex, endIndex)
    }

    suspend fun getHomeCardData(context: Context, paal: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        //Getting Values from repo
        val iyalList = mainRepository.getIyalByPaal(context, paal)
        val athikaramList = mainRepository.getAthikaramByPaal(context, paal, null)
        //Formatting Values
        val iyal = "${context.getString(R.string.iyals)}\n${iyalList.size}"
        val athikaram = "${context.getString(R.string.athikarams)}\n${athikaramList.size}"
        val kurals = "${context.getString(R.string.kurals)}\n${athikaramList.size * 10}"
        //Mapping into hashmap
        map["Iyal"] = iyal
        map["Athikaram"] = athikaram
        map["Kural"] = kurals
        return map
    }

    suspend fun getKuralRangeByPaal(context: Context, paal: String): LiveData<List<String>> {
        val range = mainRepository.getKuralRangeByPaal(context, paal)
        range?.let {
            data.value = listOf(it)
        }
        return data
    }

    suspend fun getAthikaramByPaal(context: Context, paal: String, iyal: String?): LiveData<List<String>> {
        val athikaram = mainRepository.getAthikaramByPaal(context, paal, iyal)
        data.value = athikaram
        return data
    }

    suspend fun getIyalByPaal(context: Context, paal: String): LiveData<List<String>> {
        val iyal = mainRepository.getIyalByPaal(context, paal)
        data.value = iyal
        return data
    }

    suspend fun filterKuralBySearch(searchText: String): List<Kural> {
        val kuralList = mutableListOf<Kural>()
        withContext(Dispatchers.Default) {
            kuralCache.forEach { kural ->
                val isMatched = isMatchesFound(searchText = searchText, kural = kural)
                if (isMatched) {
                    kuralList.add(kural)
                }
            }
        }
        return kuralList
    }

    suspend fun getFavoriteKurals(context: Context): LiveData<List<Kural>>{
        return mainRepository.getFavorites(context)
    }
    suspend fun manageFavorite(context: Context, kural: Kural){
        mainRepository.updateFavorite(context, kural)
    }
    suspend fun getKuralDetail(context: Context, number: Int):LiveData<Kural> {
        return mainRepository.getKuralDetail(context, number)
    }
    fun isMatchesFound(searchText: String, kural: Kural?): Boolean {
        return matchTranslation(searchText, kural) || matchTamilTranslation(searchText, kural) ||
                matchMk(searchText, kural) || matchMv(searchText, kural) ||
                matchSp(searchText, kural) || matchExplanation(searchText, kural) ||
                matchCouplet(searchText, kural) || matchKuralNo(searchText, kural)
    }

    private fun matchKuralNo(searchText: String, kural: Kural?): Boolean {
        return kural?.number.toString() == searchText
    }

    private fun matchCouplet(searchText: String, kural: Kural?): Boolean {
        return kural?.couplet?.contains(searchText) ?: false
    }

    private fun matchExplanation(searchText: String, kural: Kural?): Boolean {
        return kural?.explanation?.contains(searchText) ?: false
    }

    private fun matchSp(searchText: String, kural: Kural?): Boolean {
        return kural?.sp?.lowercase(Locale.getDefault())?.contains(searchText) ?: false
    }

    private fun matchMv(searchText: String, kural: Kural?): Boolean {
        return kural?.mv?.lowercase(Locale.getDefault())?.contains(searchText) ?: false
    }

    private fun matchMk(searchText: String, kural: Kural?): Boolean {
        return kural?.mk?.lowercase(Locale.getDefault())?.contains(searchText) ?: false
    }

    private fun matchTamilTranslation(searchText: String, kural: Kural?): Boolean {
        return kural?.tamilTranslation?.lowercase(Locale.getDefault())?.contains(searchText)
            ?: false
    }

    private fun matchTranslation(searchText: String, kural: Kural?): Boolean {
        return kural?.translation?.lowercase(Locale.getDefault())?.contains(searchText) ?: false
    }
}