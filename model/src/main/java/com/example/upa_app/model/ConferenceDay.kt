package com.example.upa_app.model

import org.threeten.bp.ZonedDateTime

data class ConferenceDay(
    val start: ZonedDateTime,
    val end: ZonedDateTime
) {
    operator fun contains(session: Session) = start <= session.startTime && end >= session.endTime
}
