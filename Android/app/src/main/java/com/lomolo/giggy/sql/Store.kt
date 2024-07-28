package com.lomolo.giggy.sql

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lomolo.giggy.model.Session
import com.lomolo.giggy.sql.dao.SessionDao
import kotlin.concurrent.Volatile

@Database(entities = [Session::class], version=1, exportSchema=false)
abstract class Store: RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    companion object {
        @Volatile
        private var Instance: Store? = null

        fun getStore(context: Context): Store {
            return Instance ?: synchronized(this) {
                Room
                    .databaseBuilder(context, Store::class.java, "store.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}