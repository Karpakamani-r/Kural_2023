package com.w2c.kural.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.w2c.kural.database.Kural
import com.w2c.kural.datasource.LocalDataSource
import com.w2c.kural.datasource.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    private val kuralListLiveData = MutableLiveData<List<Kural>>()

    suspend fun getKurals(context: Context): LiveData<List<Kural>> {
        withContext(Dispatchers.IO){
            var kuralList = localDataSource.getKuralList(context)
            if (kuralList.isEmpty()) {
                kuralList = remoteDataSource.getKuralList(context)
                localDataSource.clearKural(context)
                localDataSource.cacheKural(context, kuralList)
            }
            kuralListLiveData.postValue(kuralList)
        }
        return kuralListLiveData
    }
}