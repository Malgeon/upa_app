package com.example.upa_app.domain.sessions

import com.example.upa_app.data.pref.UserEventMessage
import com.example.upa_app.domain.FlowUseCase
import com.example.upa_app.model.Session
import com.example.upa_app.model.userdata.UserSession
import com.example.upa_app.shared.di.IoDispatcher
import com.example.upa_app.shared.result.Result
import com.example.upa_app.shared.util.TimeUtils.ConferenceDays
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import javax.inject.Inject

/**
 * Loads sorted sessions for the Schedule.
 */
open class LoadScheduleUserSessionsUseCase @Inject constructor(
    private val userEventRepository: DefaultSessionAndUserEventRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<LoadScheduleUserSessionsParameters, LoadScheduleUserSessionsResult>(dispatcher) {

    override fun execute(
        parameters: LoadScheduleUserSessionsParameters
    ): Flow<Result<LoadScheduleUserSessionsResult>> {
        Timber.d("LoadFilteredUserSessionsUseCase: Refreshing sessions with user data")
        return userEventRepository.getObservableUserEvents(parameters.userId).map { result ->
            when (result) {
                is Result.Success -> {
                    val sortedSessions = result.data.userSessions
                        .sortedWith(compareBy({ it.session.startTime }, { it.session.type }))

                    // Compute type from tags now so it's done in the background
                    sortedSessions.forEach { it.session.type }

                    val usecaseResult = LoadScheduleUserSessionsResult(
                        userSessions = sortedSessions,
                        // TODO(b/122306429) expose user events messages separately
                        userMessage = result.data.userMessage,
                        userMessageSession = result.data.userMessageSession,
                        userSessionCount = sortedSessions.size,
                        firstUnfinishedSessionIndex = findFirstUnfinishedSession(
                            sortedSessions, parameters.now
                        ),
                        dayIndexer = buildConferenceDayIndexer(sortedSessions)
                    )
                    Result.Success(usecaseResult)
                }
                is Result.Error -> {
                    Result.Error(result.exception)
                }
                is Result.Loading -> Result.Loading
            }
        }
    }

    /**
     * During the conference, find the first session which has not finished so that the UI can
     * scroll to it.
     */
    private fun findFirstUnfinishedSession(
        userSessions: List<UserSession>,
        now: ZonedDateTime
    ): Int {
        if (now.isAfter(ConferenceDays.first().start) && now.isBefore(ConferenceDays.last().end)) {
            return userSessions.indexOfFirst { it.session.endTime.isAfter(now) }
        }
        return -1
    }

    /**
     * Finds indices in [sessions] where each ConferenceDay begins. This method assumes [sessions]
     * is sorted by start time.
     */
    private fun buildConferenceDayIndexer(sessions: List<UserSession>): ConferenceDayIndexer {
        val mapping = ConferenceDays
            .associateWith { day ->
                sessions.indexOfFirst {
                    day.contains(it.session)
                }
            }
            .filterValues { it >= 0 }
        return ConferenceDayIndexer(mapping)
    }
}


data class LoadScheduleUserSessionsParameters(
    val userId: String?,
    val now: ZonedDateTime = ZonedDateTime.now()
)

data class LoadScheduleUserSessionsResult(
    val userSessions: List<UserSession>,

    /** A message to show to the user with important changes like reservation confirmations */
    val userMessage: UserEventMessage? = null,

    /** The session the user message is about, if any. */
    val userMessageSession: Session? = null,

    /** The total number of sessions. */
    val userSessionCount: Int = userSessions.size,

    /** The location of the first session which has not finished. */
    val firstUnfinishedSessionIndex: Int = -1,

    val dayIndexer: ConferenceDayIndexer
)