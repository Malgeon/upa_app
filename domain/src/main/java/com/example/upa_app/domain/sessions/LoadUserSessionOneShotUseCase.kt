package com.example.upa_app.domain.sessions

import com.example.upa_app.domain.UseCase
import com.example.upa_app.domain.userevent.DefaultSessionAndUserEventRepository
import com.example.upa_app.model.SessionId
import com.example.upa_app.model.userdata.UserSession
import com.example.upa_app.shared.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class LoadUserSessionOneShotUseCase @Inject constructor(
    private val userEventRepository: DefaultSessionAndUserEventRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Pair<String, SessionId>, UserSession>(dispatcher) {

    override suspend fun execute(parameters: Pair<String, SessionId>): UserSession {
        val (userId, eventId) = parameters

        return userEventRepository.getUserSession(userId, eventId)
    }
}