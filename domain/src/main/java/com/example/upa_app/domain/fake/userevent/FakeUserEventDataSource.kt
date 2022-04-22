package com.example.upa_app.domain.fake.userevent

import com.example.upa_app.data.BootstrapConferenceDataSource
import com.example.upa_app.domain.userevent.UserEventDataSource
import com.example.upa_app.domain.userevent.UserEventResult
import com.example.upa_app.domain.userevent.UserEventsResult
import com.example.upa_app.domain.users.ReservationRequestAction
import com.example.upa_app.domain.users.ReservationRequestAction.CancelAction
import com.example.upa_app.domain.users.ReservationRequestAction.RequestAction
import com.example.upa_app.domain.users.StarUpdatedStatus
import com.example.upa_app.domain.users.SwapRequestAction
import com.example.upa_app.model.Session
import com.example.upa_app.model.SessionId
import com.example.upa_app.model.reservations.ReservationRequestResult
import com.example.upa_app.model.reservations.ReservationRequestResult.ReservationRequestStatus.RESERVE_SUCCEEDED
import com.example.upa_app.model.userdata.UserEvent
import com.example.upa_app.shared.result.Result
import com.example.upa_app.shared.result.Result.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Returns data loaded from a local JSON file for development and testing.
 */
object FakeUserEventDataSource : UserEventDataSource {

    private val conferenceData = BootstrapConferenceDataSource.getOfflineConferenceData()!!
    private val userEvents = ArrayList<UserEvent>()

    init {
        conferenceData.sessions.forEachIndexed { i, session ->
            val reservation = ReservationRequestResult(
                RESERVE_SUCCEEDED, "123",
                System.currentTimeMillis()
            )
            if (i in 1..50) {
                userEvents.add(
                    UserEvent(
                        session.id,
                        isStarred = i % 2 == 0,
                        reservationRequestResult = reservation
                    )
                )
            }
        }
    }

    override fun getObservableUserEvents(userId: String): Flow<UserEventsResult> {
        return flow { emit(UserEventsResult(userEvents)) }
    }

    override fun getObservableUserEvent(
        userId: String,
        eventId: SessionId
    ) = flow {
        emit(UserEventResult(userEvents[0]))
    }

    override suspend fun startEvent(
        userId: String,
        userEvent: UserEvent
    ) = Success(
        if (userEvent.isStarred) StarUpdatedStatus.STARRED
        else StarUpdatedStatus.UNSTARRED
    )

    override suspend fun recordFeedbackSent(
        userId: String,
        userEvent: UserEvent
    ): Result<Unit> = Success(Unit)

    override suspend fun requestReservation(
        userId: String,
        session: Session,
        action: ReservationRequestAction
    ): Result<ReservationRequestAction> =
        Success(
            if (action is RequestAction) RequestAction() else CancelAction()
        )

    override fun getUserEvents(userId: String): List<UserEvent> {
        return userEvents
    }

    override suspend fun swapReservation(
        userId: String,
        fromSession: Session,
        toSession: Session
    ): Result<SwapRequestAction> = Success(SwapRequestAction())

    override fun getUserEvent(userId: String, eventId: SessionId): UserEvent? {
        return userEvents.firstOrNull { it.id == eventId }
    }

    override fun clearSingleEventSubscriptions() {}
}