package com.example.upa_app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * The Data Access Object for the [SessionFtsEntity] class.
 */

@Dao
interface SessionFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(sessions: List<SessionFtsEntity>)

    @Query("SELECT sessionId FROM sessionsFts WHERE sessionsFts MATCH :query")
    fun searchAllSessions(query: String): List<String>
}