package com.example.upa_app.presentation.reservation

import androidx.annotation.StringRes
import com.example.upa_app.model.userdata.UserEvent
import com.example.upa_app.presentation.R


/**
 * Models the different states of a reservation and a corresponding content description.
 */
enum class ReservationViewState(
    val state: IntArray,
    @StringRes val text: Int,
    @StringRes val contentDescription: Int
) {
    RESERVABLE(
        intArrayOf(R.attr.state_reservable),
        R.string.reservation_reservable,
        R.string.a11y_reservation_available
    ),
    WAIT_LIST_AVAILABLE(
        intArrayOf(R.attr.state_wait_list_available),
        R.string.reservation_waitlist_available,
        R.string.a11y_reservation_wait_list_available
    ),
    WAIT_LISTED(
        intArrayOf(R.attr.state_wait_listed),
        R.string.reservation_waitlisted,
        R.string.a11y_reservation_wait_listed
    ),
    RESERVED(
        intArrayOf(R.attr.state_reserved),
        R.string.reservation_reserved,
        R.string.a11y_reservation_reserved
    ),
    RESERVATION_PENDING(
        intArrayOf(R.attr.state_reservation_pending),
        R.string.reservation_pending,
        R.string.a11y_reservation_pending
    ),
    RESERVATION_DISABLED(
        intArrayOf(R.attr.state_reservation_disabled),
        R.string.reservation_disabled,
        R.string.a11y_reservation_disabled
    );

    companion object {
        fun fromUserEvent(userEvent: UserEvent?, deniedByCutoff: Boolean): ReservationViewState {
            return when {
                // Order is significant, e.g. a pending cancellation is also reserved.
                userEvent?.isReservationPending() == true ||
                        userEvent?.isCancelPending() == true -> {
                    // Treat both pending reservations & cancellations the same. This is important
                    // as the icon animations all expect to do through the same pending state.
                    RESERVATION_PENDING
                }
                userEvent?.isReserved() == true -> RESERVED
                userEvent?.isWaitlisted() == true -> WAIT_LISTED
                // TODO ?? -> WAIT_LIST_AVAILABLE
                deniedByCutoff -> RESERVATION_DISABLED
                else -> RESERVABLE
            }
        }
    }
}
