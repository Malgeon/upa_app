package com.example.upa_app.model.userdata

import com.example.upa_app.model.Session
import com.example.upa_app.model.SessionType

/**
 * Wrapper class to hold the [Session] and associating [UserEvent].
 */
data class UserSession(
    val session: Session,
    val userEvent: UserEvent
) {

    fun isPostSessionNotificationRequired(): Boolean {
        return userEvent.isReserved() &&
                !userEvent.isReviewed &&
                session.type == SessionType.SESSION
    }
}
