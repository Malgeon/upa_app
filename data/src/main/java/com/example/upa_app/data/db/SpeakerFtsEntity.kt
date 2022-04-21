package com.example.upa_app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

/**
 * This class represents Speaker data for searching with FTS.
 *
 * The [ColumnInfo] name is explicitly declared to allow flexibility for renaming the data class
 * properties without requiring changing the column name.
 */
@Entity(tableName = "speakersFts")
@Fts4
data class SpeakerFtsEntity(

    /**
     * An Fts entity table always has a column names rowid that is the equivalent of an
     * INTEGER PRIMARY KEY index. therefore, an FTS entity can only have a single field
     * annotated with PrimaryKey, it must be named rowid and must be of INTEGER affinity.
     *
     * The field can be optionally omitted in the class (as is done here).
     * but can still be used in queries.
     */

    @ColumnInfo(name = "speakerId")
    val speakerId: String,

    @ColumnInfo(name = "name")
    val name: String,

    /**
     * Body of text with the speaker's description. Note that this is named "biography" in Speaker.
     */
    @ColumnInfo(name = "description")
    val description: String
)