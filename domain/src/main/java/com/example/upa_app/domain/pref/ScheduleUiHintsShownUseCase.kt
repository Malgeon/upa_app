package com.example.upa_app.domain.pref

import com.example.upa_app.data.pref.PreferenceStorage
import com.example.upa_app.domain.UseCase
import com.example.upa_app.shared.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Returns whether the schedule UI hints have been shown.
 */
class ScheduleUiHintsShownUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Unit, Boolean>(dispatcher) {
    override suspend fun execute(parameters: Unit): Boolean =
        preferenceStorage.areScheduleUiHintsShown()

}