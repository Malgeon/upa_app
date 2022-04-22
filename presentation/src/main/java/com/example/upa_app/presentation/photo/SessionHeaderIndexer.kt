package com.example.upa_app.presentation.photo

import com.example.upa_app.model.Session
import com.example.upa_app.shared.util.TimeUtils
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit

/**
 * Find the first session at each start time (rounded down to nearest minute) and return pairs of
 * index to start time. Assumes that [sessions] are sorted by ascending start time.
 */

fun indexSessionHeaders(sessions: List<Session>, zoneId: ZoneId): List<Pair<Int, ZonedDateTime>> {
    return sessions
        .mapIndexed { index, session ->
            index to TimeUtils.zonedTime(session.startTime, zoneId)
        }
        .distinctBy { it.second.truncatedTo(ChronoUnit.MINUTES) }
}