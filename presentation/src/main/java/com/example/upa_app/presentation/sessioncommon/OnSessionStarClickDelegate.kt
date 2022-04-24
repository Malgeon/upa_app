package com.example.upa_app.presentation.sessioncommon

import com.example.upa_app.domain.users.StarEventAndNotifyUseCase
import com.example.upa_app.domain.users.StarEventParameter
import com.example.upa_app.model.userdata.UserSession
import com.example.upa_app.presentation.R
import com.example.upa_app.presentation.messages.SnackbarMessage
import com.example.upa_app.presentation.messages.SnackbarMessageManager
import com.example.upa_app.presentation.signin.SignInViewModelDelegate
import com.example.upa_app.shared.analytics.AnalyticsActions
import com.example.upa_app.shared.analytics.AnalyticsHelper
import com.example.upa_app.shared.di.ApplicationScope
import com.example.upa_app.shared.di.MainDispatcher
import com.example.upa_app.shared.result.Result
import com.example.upa_app.shared.util.tryOffer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject


/**
 * A delegate providing common functionality for starring events.
 */

interface OnSessionStarClickDelegate : OnSessionStarClickListener {
    val navigateToSignInDialogEvents: Flow<Unit>
}

class DefaultOnSessionStarClickDelegate @Inject constructor(
    signInViewModelDelegate: SignInViewModelDelegate,
    private val starEventUseCase: StarEventAndNotifyUseCase,
    private val snackbarMessageManager: SnackbarMessageManager,
    private val analyticsHelper: AnalyticsHelper,
    @ApplicationScope private val externalScope: CoroutineScope,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : OnSessionStarClickDelegate, SignInViewModelDelegate by signInViewModelDelegate {

    private val _navigateToSignInDialogEvents = Channel<Unit>(capacity = Channel.CONFLATED)
    override val navigateToSignInDialogEvents = _navigateToSignInDialogEvents.receiveAsFlow()

    override fun onStarClicked(userSession: UserSession) {
        if (!isUserSignedInValue) {
            Timber.d("Showing Sign-in dialog after star click")
            _navigateToSignInDialogEvents.tryOffer(Unit)
            return
        }
        val newIsStarredState = !userSession.userEvent.isStarred

        // Update the snackbar message optimistically.
        val stringResId = if (newIsStarredState) {
            R.string.event_starred
        } else {
            R.string.event_unstarred
        }
        snackbarMessageManager.addMessage(
            SnackbarMessage(
                messageId = stringResId,
                actionId = R.string.dont_show,
                requestChangeId = UUID.randomUUID().toString()
            )
        )
        if (newIsStarredState) {
            analyticsHelper.logUiEvent(userSession.session.title, AnalyticsActions.STARRED)
        }

        externalScope.launch(mainDispatcher) {
            userIdValue?.let {
                val result = starEventUseCase(
                    StarEventParameter(
                        it,
                        userSession.copy(
                            userEvent = userSession.userEvent.copy(isStarred = newIsStarredState)
                        )
                    )
                )
                // SHow and error message if a star request fails
                if (result is Result.Error) {
                    snackbarMessageManager.addMessage(SnackbarMessage(R.string.event_star_error))
                }
            }
        }
    }
}