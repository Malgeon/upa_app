package com.example.upa_app.domain.users

import com.example.upa_app.domain.UseCase
import com.example.upa_app.domain.userevent.SessionAndUserEventRepository
import com.example.upa_app.model.SessionId
import com.example.upa_app.shared.di.IoDispatcher
import com.example.upa_app.shared.result.Result.Error
import com.example.upa_app.shared.result.Result.Success
import com.example.upa_app.shared.result.Result.Loading
import kotlinx.coroutines.CoroutineDispatcher
import java.lang.IllegalStateException
import javax.inject.Inject

/**
 * Sends a request to replace reservations.
 */
open class SwapActionUseCase @Inject constructor(
    private val repository: SessionAndUserEventRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : UseCase<SwapRequestParameters, SwapRequestAction>(ioDispatcher) {

    override suspend fun execute(parameters: SwapRequestParameters): SwapRequestAction {
        val (userId, sessionId, _, toId) = parameters
        return when (
            val updateResult = repository.swapReservation(userId, sessionId, toId)
        ) {
            is Success -> updateResult.data
            is Error -> throw updateResult.exception
            Loading -> throw IllegalStateException()
        }
    }
}

/**
 * Parameters required to process the swap reservations request.
 */
data class SwapRequestParameters(
    val userId: String,
    val fromId: SessionId,
    val fromTitle: String,
    val toId: SessionId,
    val toTitle: String
)

class SwapRequestAction