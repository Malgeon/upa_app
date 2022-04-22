package com.example.upa_app.domain.search

import androidx.core.os.trace
import com.example.upa_app.domain.FlowUseCase
import com.example.upa_app.domain.prefs.UserSessionFilterMatcher
import com.example.upa_app.domain.userevent.SessionAndUserEventRepository
import com.example.upa_app.model.filters.Filter
import com.example.upa_app.model.userdata.UserSession
import com.example.upa_app.shared.di.IoDispatcher
import com.example.upa_app.shared.result.Result
import com.example.upa_app.shared.result.Result.Success
import com.example.upa_app.shared.result.Result.Loading
import com.example.upa_app.shared.result.Result.Error
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class SessionSearchUseCaseParams(
    val userId: String?,
    val query: String,
    val filters: List<Filter>
)

class SessionSearchUseCase @Inject constructor(
    private val repository: SessionAndUserEventRepository,
    private val textMatchStrategy: SessionTextMatchStrategy,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<SessionSearchUseCaseParams, List<UserSession>>(dispatcher) {

    override fun execute(parameters: SessionSearchUseCaseParams): Flow<Result<List<UserSession>>> {
        val (userId, query, filters) = parameters
        trace("search-path-usercase") {
            val filterMatcher = UserSessionFilterMatcher(filters)
            return repository.getObservableUserEvents(userId).map { result ->
                when (result) {
                    is Success -> {
                        val searchResults = textMatchStrategy.searchSessions(
                            result.data.userSessions, query
                        ).filter { filterMatcher.matches(it) }
                        Success(searchResults)
                    }
                    is Loading -> result
                    is Error -> result
                }
            }
        }
    }
}