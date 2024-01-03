package com.w2c.kural.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface KuralDAO {
    @Insert
    fun insertKuralData(kural: Kural)

    @Query("delete from Kural")
    fun deleteData()

    @Update
    fun updateKuralData(kural: Kural): Int

    @get:Query("select * from Kural")
    val allKural: List<Kural>

    @Query("select * from Kural where number=:number")
    fun getKural(number: Int): Kural?

    @Query("select * from Kural where number between :startIndex and :endIndex")
    fun getKuralByRange(startIndex: Int, endIndex: Int): List<Kural>

    @get:Query("select * from Kural where favourite=1")
    val favKuralList: List<Kural>

    @get:Query("select * from Kural where line1=line1")
    val kuralWord: List<Kural>
}