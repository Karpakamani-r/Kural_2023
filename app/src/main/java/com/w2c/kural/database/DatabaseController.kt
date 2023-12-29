package com.w2c.kural.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.w2c.kural.utils.DB_VERSION


@Database(entities = [Kural::class], version = 1, exportSchema = false)
abstract class DatabaseController : RoomDatabase() {
    abstract val kuralDAO: KuralDAO

    companion object {
        private lateinit var mDBController: DatabaseController
        @Synchronized
        fun getInstance(context: Context): DatabaseController {
            if (!Companion::mDBController.isInitialized) {
                mDBController = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseController::class.java,
                    "KuralDB"
                ).build()
            }
            return mDBController
        }
    }
}