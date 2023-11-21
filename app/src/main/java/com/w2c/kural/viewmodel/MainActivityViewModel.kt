package com.w2c.kural.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.w2c.kural.database.Kural
import com.w2c.kural.repository.MainRepository
import java.util.*

class MainActivityViewModel(private val mainRepository: MainRepository) :
    ViewModel() {

    suspend fun getKurals(context: Context): LiveData<List<Kural>> {
        return mainRepository.getKurals(context)
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