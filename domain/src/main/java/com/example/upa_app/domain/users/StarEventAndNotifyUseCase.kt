package com.example.upa_app.domain.users

import com.example.upa_app.domain.UseCase
import com.example.upa_app.domain.sessions.StarReserveNotificationAlarmUpdater
import com.example.upa_app.domain.userevent.SessionAndUserEventRepository
import com.example.upa_app.model.userdata.UserSession
import com.example.upa_app.shared.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class StarEventAndNotifyUseCase @Inject constructor(
    private val repository: SessionAndUserEventRepository,
    private val alarmUpdater: StarReserveNotificationAlarmUpdater,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : UseCase<StarEventParameter, StarUpdatedStatus>(ioDispatcher) {

    override suspend fun execute(parameters: StarEventParameter): StarUpdatedStatus {
        TODO("Not yet implemented")
    }
}

data class StarEventParameter(
    val userId: String,
    val userSession: UserSession
)

enum class StarUpdatedStatus {
    STARRED,
    UNSTARRED
}
