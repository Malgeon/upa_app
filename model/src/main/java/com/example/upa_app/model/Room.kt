package com.example.upa_app.model

/**
 * Describes a venue associated with the conference.
 */
data class Room(
    /**
     * Unique string identifying this room.
     */
    val id: String,

    /**
     * The name of the room.
     */
    val name: String
) {
    val abbreviatedName
        get() = name.split("|")[0]
}
