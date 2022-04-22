package com.example.upa_app.domain.settings

import com.example.upa_app.data.pref.PreferenceStorage
import com.example.upa_app.domain.UseCase
import com.example.upa_app.shared.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetTimeZoneUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Unit, Boolean>(dispatcher) {
    override suspend fun execute(parameters: Unit) =
        preferenceStorage.preferConferenceTimeZone.first()
}