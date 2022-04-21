package com.example.upa_app.domain.sessions

import com.example.upa_app.data.pref.UserEventMessage
import com.example.upa_app.domain.userevent.DefaultSessionAndUserEventRepository
import com.example.upa_app.domain.FlowUseCase
import com.example.upa_app.model.SessionId
import com.example.upa_app.model.userdata.UserSession
import com.example.upa_app.shared.di.IoDispatcher
import com.example.upa_app.shared.result.Result
import com.example.upa_app.shared.result.Result.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@ExperimentalCoroutinesApi
open class LoadUserSessionUseCase @Inject constructor(
    private val userEventRepository: DefaultSessionAndUserEventRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : FlowUseCase<Pair<String?, SessionId>, LoadUserSessionUseCaseResult>(ioDispatcher) {

    override fun execute(parameters: Pair<String?, SessionId>):
            Flow<Result<LoadUserSessionUseCaseResult>> {
        val (userId, eventId) = parameters
        return userEventRepository.getObservableUserEvent(userId, eventId).map {
            if (it is Success) {
                Success(LoadUserSessionUseCaseResult(userSession = it.data.userSession))
            } else {
                it
            }
        }
    }
}

data class LoadUserSessionUseCaseResult(
    val userSession: UserSession,

    /** A message to show to the user with important changes like reservation confirmations */
    val userMessage: UserEventMessage? = null
)
