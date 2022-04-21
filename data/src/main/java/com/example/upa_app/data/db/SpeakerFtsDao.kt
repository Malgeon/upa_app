package com.example.upa_app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * The Data Access Object for the [SpeakerFtsEntity] class.
 */
@Dao
interface SpeakerFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(speakers: List<SpeakerFtsEntity>)

    @Query("SELECT speakerId FROM speakersFts WHERE speakersFts MATCH :query")
    fun searchAllSpeakers(query: String): List<String>
}