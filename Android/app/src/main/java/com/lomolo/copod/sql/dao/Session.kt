package com.lomolo.copod.sql.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lomolo.copod.model.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(vararg session: Session)

    @Query("SELECT * FROM sessions LIMIT 1")
    fun get(): Flow<List<Session>>

    @Update
    suspend fun update(vararg session: Session)

    @Query("DELETE FROM sessions")
    suspend fun delete()
}