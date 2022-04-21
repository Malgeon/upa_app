package com.example.upa_app.domain.sessions

import com.example.upa_app.domain.UseCase
import com.example.upa_app.model.Session
import com.example.upa_app.model.SessionId
import com.example.upa_app.shared.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

open class LoadSessionOneShotUseCase @Inject constructor(
    private val repository: SessionRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<SessionId, Session>(dispatcher){

    override suspend fun execute(parameters: SessionId): Session {
        return repository.getSession(parameters)
    }
}