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
    fun updateKuralData(kural: Kural)

    @get:Query("select * from Kural")
    val allKural: MutableList<Kural>

    @Query("select * from Kural where number=:number")
    fun getKural(number: Int): Kural?

    @get:Query("select * from Kural where favourite=1")
    val favKuralList: MutableList<Kural>

    @get:Query("select * from Kural where line1=line1")
    val kuralWord: MutableList<Kural>
}