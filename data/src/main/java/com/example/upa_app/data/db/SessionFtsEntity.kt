package com.example.upa_app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

/**
 * This class represents [Session] data for searching with FTS
 *
 * the [ColumnInfo] name is explicitly declared to allow flexibility for renaming the data class
 * properties without requiring changing the column name.
 */
@Entity(tableName = "sessionsFts")
@Fts4
class SessionFtsEntity(

    /**
     * An FTS entity table always has a column named rowid that is the equivalent of an
     * INTEGER PRIMARY KEY index. Therefore, an FTS entity can only have a single field
     * annotated with PrimaryKey, it must be named rowid and must be of INTEGER affinity.
     *
     * The field can be optionally omitted in the class (as is done here),
     * but can still be used in queries.
     */

    /**
     * Unique string identifying this session.
     */
    @ColumnInfo(name = "sessionId")
    val sessionId: String,

    /**
     * Session title.
     */
    @ColumnInfo(name = "title")
    val title: String,

    /**
     * Body of text with the session's description.
     */
    @ColumnInfo(name = "description")
    val description: String,

    /**
     * The session speaker(s), stored as a CSV String.
     */
    @ColumnInfo(name = "speakers")
    val speakers: String
)