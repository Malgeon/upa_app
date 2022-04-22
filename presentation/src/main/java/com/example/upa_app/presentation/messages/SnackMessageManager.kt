package com.example.upa_app.presentation.messages

import androidx.annotation.VisibleForTesting
import com.example.upa_app.data.pref.PreferenceStorage
import com.example.upa_app.domain.prefs.StopSnackbarActionUseCase
import com.example.upa_app.shared.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class SnackMessageManager @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @ApplicationScope private val coroutineScope: CoroutineScope,
    private val stopSnackberActionUseCase: StopSnackbarActionUseCase
) {
    companion object {
        // Keep a fixed number of old items
        @VisibleForTesting
        const val MAX_ITEMS = 10
    }
}