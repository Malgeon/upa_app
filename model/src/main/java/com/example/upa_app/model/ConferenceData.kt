package com.example.upa_app.model

/**
 * Contains schedule data with the UI models.
 */
data class ConferenceData(
    val sessions: List<Session>,
    val speakers: List<Speaker>,
    val rooms: List<Room>,
    val codelabs: List<Codelab>,
    val tags: List<Tag>,
    val version: Int
)
