package com.example.upa_app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

/**
 * This class represents Codelab data for searching with FTS.
 *
 * The [ColumnInfo] name is explicitly declared to allow flexibility for renaming the data class
 * properties without requiring changing the column name.
 */
@Entity(tableName = "codelabsFts")
@Fts4
data class CodelabFtsEntity(

    /**
     * An FTS entity table always has a column named rowid that is the equivalent of an
     * INTEGER PRIMARY KEY index. Therefore, an FTS entity can only have a single field
     * annotated with PrimaryKey, it must be named rowid and must be of INTEGER affinity.
     *
     * The field can be optionally omitted in the class (as is done here),
     * but can still be used in queries.
     */

    @ColumnInfo(name = "codelabId")
    val codelabId: String,

    @ColumnInfo(name = "title")
    val title: String,

    /**
     * Body of text with the codelab's description.
     */
    @ColumnInfo(name = "description")
    val description: String
)