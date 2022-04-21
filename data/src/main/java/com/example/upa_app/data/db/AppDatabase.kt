package com.example.upa_app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The [Room] database for this app.
 */
@Database(
    entities = [
        SessionFtsEntity::class,
        SpeakerFtsEntity::class,
        CodelabFtsEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionFtsDao(): SessionFtsDao
    abstract fun speakerFtsDao(): SpeakerFtsDao
    abstract fun codelabFtsDao(): CodelabFtsDao

    companion object {
        private const val databaseName = "schedule-db"

        fun buildDatabase(context: Context): AppDatabase {
            // Since Room is only used for FTS, destructive migration is enough because the tables
            // are cleared every time the app launches.
            // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
            return Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}