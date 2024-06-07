package com.lomolo.giggy.sql.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lomolo.giggy.model.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(session: Session)

    @Query("SELECT * FROM sessions LIMIT 1")
    fun get(): Flow<List<Session>>

    @Query("DELETE FROM sessions")
    suspend fun delete()
}