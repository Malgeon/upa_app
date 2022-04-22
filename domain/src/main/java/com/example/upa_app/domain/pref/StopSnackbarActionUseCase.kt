package com.example.upa_app.domain.pref

import com.example.upa_app.data.pref.PreferenceStorage
import com.example.upa_app.domain.UseCase
import com.example.upa_app.shared.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case to update the preference whether Snackbar messages should be stopped.
 * Only the Snackbars that have R.string.dont_show actionId as its action, the rest
 * of the Snackbars should still show even after setting this preference as true.
 */
open class StopSnackbarActionUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Boolean, Unit>(dispatcher){
    override suspend fun execute(parameters: Boolean) {
        preferenceStorage.stopSnackbar(parameters)
    }
}