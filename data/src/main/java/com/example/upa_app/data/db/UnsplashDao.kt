package com.example.upa_app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UnsplashDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(speakers: List<UnsplashEntity>)

    @Query("SELECT speakerId FROM speakersFts WHERE speakersFts MATCH :query")
    fun searchAll(query: String): List<UnsplashEntity>
}