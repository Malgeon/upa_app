package com.example.upa_app.domain

import com.example.upa_app.data.ConferenceDataRepository
import com.example.upa_app.shared.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject

/**
 * Forces a refresh in the conference data repository.
 */
open class RefreshConferenceDataUseCase @Inject constructor(
    private val repository: ConferenceDataRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Any, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Any): Boolean {
        try {
            repository.refreshCacheWithRemoteConferenceData()
        } catch (e: Exception) {
            Timber.e(e, "Conference data refresh failed")
            throw e
        }
        return true
    }
}
