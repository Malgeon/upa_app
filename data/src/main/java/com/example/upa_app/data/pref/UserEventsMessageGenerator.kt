package com.example.upa_app.data.pref

data class UserEventMessage(
    val type: UserEventMessageChangeType,
    val sessionId: String? = null,
    val changeRequestId: String? = null
)

enum class UserEventMessageChangeType {
    CHANGES_IN_RESERVATIONS,
    RESERVATIONS_REPLACED,
    CHANGES_IN_WAITLIST,
    RESERVATION_CANCELED,
    WAITLIST_CANCELED,
    RESERVATION_DENIED_CUTOFF,
    RESERVATION_DENIED_CLASH,
    RESERVATION_DENIED_UNKNOWN,
    CANCELLATION_DENIED_CUTOFF,
    CANCELLATION_DENIED_UNKNOWN
}