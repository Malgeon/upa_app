package com.example.upa_app.data.session

import com.example.upa_app.data.ConferenceDataRepository
import com.example.upa_app.model.ConferenceDay
import com.example.upa_app.model.Session
import com.example.upa_app.model.SessionId
import javax.inject.Inject

interface SessionRepository {
    fun getSessions(): List<Session>
    fun getSession(eventId: SessionId): Session
    fun getConferenceDays(): List<ConferenceDay>
}

class DefaultSessionRepository @Inject constructor(
    private val conferenceDataRepository: ConferenceDataRepository
) : SessionRepository {

    override fun getSessions(): List<Session> {
        return conferenceDataRepository.getOfflineConferenceData().sessions
    }

    override fun getSession(eventId: SessionId): Session {
        return conferenceDataRepository.getOfflineConferenceData().sessions.firstOrNull { session ->
            session.id == eventId
        } ?: throw SessionNotFoundException()
    }

    override fun getConferenceDays(): List<ConferenceDay> {
        return conferenceDataRepository.getConferenceDays()
    }
}

class SessionNotFoundException : Throwable()