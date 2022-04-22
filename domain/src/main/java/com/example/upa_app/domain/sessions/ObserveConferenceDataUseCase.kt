package com.example.upa_app.domain.sessions

import com.example.upa_app.data.ConferenceDataRepository
import com.example.upa_app.domain.FlowUseCase
import com.example.upa_app.shared.di.IoDispatcher
import com.example.upa_app.shared.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Forces a refresh in the conference data repository.
 */
open class ObserveConferenceDataUseCase @Inject constructor(
    private val repository: ConferenceDataRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Long>(dispatcher){
    override fun execute(parameters: Unit): Flow<Result<Long>> =
        repository.dataLastUpDatedObservable.map {
            Result.Success(it)
        }
}