package com.example.upa_app.presentation.sessioncommon

import com.example.upa_app.data.pref.UserEventMessageChangeType
import com.example.upa_app.data.pref.UserEventMessageChangeType.CANCELLATION_DENIED_CUTOFF
import com.example.upa_app.data.pref.UserEventMessageChangeType.CANCELLATION_DENIED_UNKNOWN
import com.example.upa_app.data.pref.UserEventMessageChangeType.CHANGES_IN_RESERVATIONS
import com.example.upa_app.data.pref.UserEventMessageChangeType.CHANGES_IN_WAITLIST
import com.example.upa_app.data.pref.UserEventMessageChangeType.RESERVATIONS_REPLACED
import com.example.upa_app.data.pref.UserEventMessageChangeType.RESERVATION_CANCELED
import com.example.upa_app.data.pref.UserEventMessageChangeType.RESERVATION_DENIED_CLASH
import com.example.upa_app.data.pref.UserEventMessageChangeType.RESERVATION_DENIED_CUTOFF
import com.example.upa_app.data.pref.UserEventMessageChangeType.RESERVATION_DENIED_UNKNOWN
import com.example.upa_app.data.pref.UserEventMessageChangeType.WAITLIST_CANCELED
import com.example.upa_app.presentation.R

fun UserEventMessageChangeType.stringRes(): Int {
    return when (this) {
        CHANGES_IN_RESERVATIONS -> R.string.reservation_new
        RESERVATIONS_REPLACED -> R.string.reservation_replaced
        CHANGES_IN_WAITLIST -> R.string.waitlist_new
        RESERVATION_CANCELED -> R.string.reservation_cancel_succeeded
        WAITLIST_CANCELED -> R.string.waitlist_cancel_succeeded
        RESERVATION_DENIED_CUTOFF -> R.string.reservation_denied_cutoff
        RESERVATION_DENIED_CLASH -> R.string.reservation_denied_clash
        RESERVATION_DENIED_UNKNOWN -> R.string.reservation_denied_unknown
        CANCELLATION_DENIED_CUTOFF -> R.string.cancellation_denied_cutoff
        CANCELLATION_DENIED_UNKNOWN -> R.string.cancellation_denied_unknown
    }
}