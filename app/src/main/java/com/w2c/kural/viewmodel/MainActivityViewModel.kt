package com.w2c.kural.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.w2c.kural.R
import com.w2c.kural.database.Kural
import com.w2c.kural.model.Setting
import com.w2c.kural.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import com.w2c.kural.utils.ATHIKARAM
import com.w2c.kural.utils.IYAL
import com.w2c.kural.utils.KURAL
import com.w2c.kural.utils.NotificationPreference

class MainActivityViewModel(private val mainRepository: MainRepository) :
    ViewModel() {

    private val kuralCache_: MutableList<Kural> = mutableListOf()
    val kuralCache: List<Kural> = kuralCache_

    private val favClickLiveData_: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val favClickLiveData: LiveData<Boolean> = favClickLiveData_

    private val favUpdateTBIconLiveData_: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val favUpdateTBIconLiveData: LiveData<Boolean> = favUpdateTBIconLiveData_

    private val favStatusLiveData_: MutableLiveData<Array<Boolean>> = MutableLiveData<Array<Boolean>>()
    val favStatusLiveData: LiveData<Array<Boolean>> = favStatusLiveData_

    private val notificationLiveData_: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val notificationLiveData: LiveData<Boolean> = notificationLiveData_

    private val notificationRefreshUILiveData_: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val notificationRefreshUILiveData: LiveData<Boolean> = notificationRefreshUILiveData_

    private val data: MutableLiveData<List<String>> = MutableLiveData<List<String>>()

    private lateinit var favoriteKural: Kural
    fun cacheKural(list: List<Kural>) {
        kuralCache_.clear()
        kuralCache_.addAll(list)
    }

    suspend fun getKurals(
        context: Context
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
        map[IYAL] = iyal
        map[ATHIKARAM] = athikaram
        map[KURAL] = kurals
        return map
    }

    suspend fun getKuralRangeByPaal(context: Context, paal: String): LiveData<List<String>> {
        val range = mainRepository.getKuralRangeByPaal(context, paal)
        range?.let {
            data.value = listOf(it)
        }
        return data
    }

    suspend fun getAthikaramByPaal(
        context: Context,
        paal: String,
        iyal: String?
    ): LiveData<List<String>> {
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

    suspend fun getFavoriteKurals(context: Context): LiveData<List<Kural>> {
        return mainRepository.getFavorites(context)
    }

    suspend fun manageFavorite(context: Context, kural: Kural) {
        kuralCache_.set(kural.number - 1, kural)
        val statusCode = mainRepository.updateFavorite(context, kural)
        //(statusCode>0) refers db update status, (kural.favourite == 1) referes add/remove process
        val res = arrayOf(statusCode > 0, kural.favourite == 1)
        favStatusLiveData_.value = res
    }

    fun onFavClick() {
        favClickLiveData_.value = true
    }

    fun updateFavToolBarIcon(visible: Boolean) {
        favUpdateTBIconLiveData_.value = visible
    }

    suspend fun getKuralDetail(context: Context, number: Int): LiveData<Kural> {
        return mainRepository.getKuralDetail(context, number)
    }

    fun observeNotificationChanges() {
        notificationLiveData_.value = true
    }

    fun updateNotificationStatus(context: Context): Boolean{
        val prefs = NotificationPreference.getInstance(context)
        prefs.isDailyNotifyValue = !prefs.isDailyNotifyValue
        return prefs.isDailyNotifyValue
    }

    fun getSettingsData(context: Context): List<Setting> {
        return mainRepository.getSettingsData(context)
    }

    fun notificationUpdateUI(){
        notificationRefreshUILiveData_.value = true
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