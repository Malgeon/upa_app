package com.example.upa_app.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.upa_app.presentation.signin.SignInViewModelDelegate
import com.example.upa_app.shared.util.tryOffer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    signInViewModelDelegate: SignInViewModelDelegate,
    @ApplicationContext context: Context
) : ViewModel(),
    SignInViewModelDelegate by signInViewModelDelegate {

    private val _navigationActions = Channel<MainNavigationAction>(Channel.CONFLATED)
    val navigationActions = _navigationActions.receiveAsFlow()

    fun onProfileClicked() {
        if (isUserSignedInValue) {
            _navigationActions.tryOffer(MainNavigationAction.OpenSignOut)
        } else {
            _navigationActions.tryOffer(MainNavigationAction.OpenSignIn)
        }
    }
}

sealed class MainNavigationAction {
    object OpenSignIn : MainNavigationAction()
    object OpenSignOut : MainNavigationAction()
}