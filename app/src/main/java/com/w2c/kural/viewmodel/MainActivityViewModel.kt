package com.w2c.kural.viewmodel

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.w2c.kural.R
import com.w2c.kural.database.Kural
import com.w2c.kural.repository.MainRepository
import com.w2c.kural.utils.Menus
import java.util.*
import kotlin.collections.AbstractList

class MainActivityViewModel(private val mainRepository: MainRepository) :
    ViewModel() {

    private val kuralCache_: MutableList<Kural> = mutableListOf()
    val kuralCache: List<Kural> = kuralCache_
    fun cacheKural(list: List<Kural>) {
        kuralCache_.clear()
        kuralCache_.addAll(list)
    }

    suspend fun getKurals(context: Context): LiveData<List<Kural>> {
        return mainRepository.getKurals(context)
    }

    suspend fun getHomeCardData(context: Context, paal: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        //Getting Values from repo
        val iyalList = mainRepository.getIyalByPaal(context, paal)
        val athikaramList = mainRepository.getAthikaramByPaal(context, paal)
        //Formatting Values
        val iyal = "${context.getString(R.string.iyal)}\n${iyalList.size}"
        val athikaram = "${context.getString(R.string.athikaram)}\n${athikaramList.size}"
        val kurals = "${context.getString(R.string.kurals)}\n${athikaramList.size * 10}"
        //Mapping into hashmap
        map["Iyal"] = iyal
        map["Athikaram"] = athikaram
        map["Kural"] = kurals
        return map
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