package com.example.upa_app.domain.sessions

import com.example.upa_app.model.ConferenceDay


class ConferenceDayIndexer(
    /**
     * Mapping of ConferenceDay to start position.
     * Values (indexes) must be >= 0 and in ascending order.
     */
    mapping: Map<ConferenceDay, Int>
) {
    init {
        var previous = -1
        mapping.forEach { (_, value) ->
            if (value <= previous) {
                throw IllegalStateException("Index values must be >= 0 and in ascending order.")
            }
            previous = value
        }
    }

    /** The ConferenceDays that are indexed. */
    val days = mapping.map { it.key }
    private val startPositions = mapping.map { it.value }

    fun dayForPosition(position: Int): ConferenceDay? {
        startPositions.asReversed().forEachIndexed { index, intVal ->
            if (intVal <= position) {
                // Indexes are inverted because of asReversed()
                return days[days.size - index - 1]
            }
        }
        return null
    }

    fun positionForDay(day: ConferenceDay): Int {
        val index = days.indexOf(day)
        if (index == -1) {
            throw IllegalStateException("Unknown day")
        }
        return startPositions[index]
    }
}