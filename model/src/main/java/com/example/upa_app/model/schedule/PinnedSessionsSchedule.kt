package com.example.upa_app.model.schedule

/**
 * Class representation of JSON format passed to the AR module for showing user's pinned sessions.
 * The field name must not be changed.
 */
data class PinnedSessionsSchedule(val schedule: List<PinnedSession>)

/**
 * Class representation of JSON format for a single session.
 * The field names must not be changed.
 *
 * Example entity is like
 * {
 *    "name": "session1",
 *    "location": "Room 1",
 *    "day": "3/29",
 *    "time": "13:30",
 *    "timestamp": 82547983,
 *    "description": "Session description"
 * }
 */
data class PinnedSession(
    val name: String,
    val location: String,
    val day: String,
    val time: String,
    val timestamp: Long,
    val description: String
)
