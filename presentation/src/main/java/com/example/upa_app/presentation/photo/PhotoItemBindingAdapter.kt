package com.example.upa_app.presentation.photo

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.upa_app.model.Room
import com.example.upa_app.model.userdata.UserEvent
import com.example.upa_app.presentation.R
import com.example.upa_app.presentation.reservation.ReservationTextView
import com.example.upa_app.presentation.reservation.ReservationViewState
import com.example.upa_app.shared.util.TimeUtils
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

@BindingAdapter(
    "sessionStart",
    "timeZoneId",
    "showTime",
    "sessionRoom",
    requireAll = true
)
fun sessionDateTimeLocation(
    textView: TextView,
    startTime: ZonedDateTime?,
    zoneId: ZoneId?,
    showTime: Boolean,
    room: Room?
) {
    startTime ?: return
    zoneId ?: return
    val roomName = room?.name ?: "-"
    val localStartTime = TimeUtils.zonedTime(startTime, zoneId)

    // For a11y, always use date, time, and location -> "May 7, 10:10 AM / Amphitheatre
    val dateTimeString = TimeUtils.dateTimeString(localStartTime)
    val contentDescription = textView.resources.getString(
        R.string.session_duration_location,
        dateTimeString,
        roomName
    )
    textView.contentDescription = contentDescription

    textView.text = if (showTime) {
        // Show date, time, and location, so just reuse the content description
        contentDescription
    } else if (!TimeUtils.isConferenceTimeZone(zoneId)) {
        // Show date and location -> "May 7 / Amphitheatre"
        val dateString = TimeUtils.dateString(localStartTime)
        textView.resources.getString(
            R.string.session_duration_location,
            dateString,
            roomName
        )
    } else {
        // Show location only
        roomName
    }
}

@BindingAdapter(
    "reservationStatus",
    "showReservations",
    "isReservable",
    requireAll = true
)
fun setReservationStatus(
    textView: ReservationTextView,
    userEvent: UserEvent?,
    showReservations: Boolean,
    isReservable: Boolean
) {
    when (isReservable && showReservations) {
        true -> {
            val reservationUnavailable = false // TODO determine this condition
            textView.status = ReservationViewState.fromUserEvent(userEvent, reservationUnavailable)
            textView.visibility = View.VISIBLE
        }
        false -> {
            textView.visibility = View.GONE
        }
    }
}