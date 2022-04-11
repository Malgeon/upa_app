package com.example.upa_app.presentation.signin

import android.net.Uri
import com.example.upa_app.data.signin.AuthenticatedUserInfo
import com.example.upa_app.domain.auth.ObserveUserAuthStateUseCase
import com.example.upa_app.domain.pref.NotificationsPrefIsShownUseCase
import com.example.upa_app.presentation.signin.SignInNavigationAction.RequestSignIn
import com.example.upa_app.presentation.signin.SignInNavigationAction.RequestSignOut
import com.example.upa_app.presentation.util.WhileViewSubscribed
import com.example.upa_app.shared.di.ApplicationScope
import com.example.upa_app.shared.di.IoDispatcher
import com.example.upa_app.shared.di.MainDispatcher
import com.example.upa_app.shared.di.ReservationEnabledFlag
import com.example.upa_app.shared.result.Result
import com.example.upa_app.shared.result.data
import com.example.upa_app.shared.util.tryOffer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

enum class SignInNavigationAction {
    RequestSignIn, RequestSignOut, ShowNotificationPreferencesDialog
}

/**
 * Interface to implement sign-in functionality in a ViewModel.
 *
 * you can inject a implementation of this via Dagger2, then use the implementation as an interface
 * delegate to add sign in functionality without writing any code
 *
 * Example usage
 *
 * ```
 * class MyViewModel @Injecty constructor(
 *     signInViewModelComponent: SignInViewModelDelegate
 * ) : ViewModel(), SignInViewModelDelegate by signInViewModelComponent {
 * ```
 *
 */
interface SignInViewModelDelegate {
    /**
     * Live updated value of the current firebase user
     */
    val userInfo: StateFlow<AuthenticatedUserInfo?>

    /**
     * Live updated value of the current firebase users image url
     */
    val currentUserImageUri: StateFlow<Uri?>

    /**
     * Emits Event when a sign-in event should be attempted or a dialog shown
     */
    val signInNavigationActions: Flow<SignInNavigationAction>

    /**
     * Emits whether or not to show reservations for the current user
     */
    val showReservations: StateFlow<Boolean>

    /**
     * Emit an Event on performSignInEvent to request sign-in
     */
    suspend fun emitSignInRequest()

    /**
     * Emit an Event on performSignInEvent to request sign-out
     */
    suspend fun emitSignOutRequest()

    val userId: Flow<String?>

    /**
     * Returns the current user ID or null if not available.
     */
    val userIdValue: String?

    val isUserSignedIn: StateFlow<Boolean>

    val isUserSignedInValue: Boolean

    val isUserRegistered: StateFlow<Boolean>

    val isUserRegisteredValue: Boolean
}

/**
 * Implementation of SignInViewModelDelegate that uses Firebase's auth mechanism.
 */
internal class FirebaseSignInViewModelDelegate @Inject constructor(
    observeUserAuthStateUseCase: ObserveUserAuthStateUseCase,
    private val notificationsPrefIsShownUseCase: NotificationsPrefIsShownUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @ReservationEnabledFlag val isReservationEnabledByRemoteConfig: Boolean,
    @ApplicationScope val applicationScope: CoroutineScope
) : SignInViewModelDelegate {

    private val _signInNavigationActions = Channel<SignInNavigationAction>(Channel.CONFLATED)
    override val signInNavigationActions = _signInNavigationActions.receiveAsFlow()

    private val currentFirebaseUser: Flow<Result<AuthenticatedUserInfo?>> =
        observeUserAuthStateUseCase(Any()).map {
            if (it is Result.Error) {
                Timber.e(it.exception)
            }
            Timber.e(it.toString())
            it
        }

    override val userInfo: StateFlow<AuthenticatedUserInfo?> = currentFirebaseUser.map {
        Timber.e("${it.data}")
        (it as? Result.Success)?.data
    }.stateIn(applicationScope, WhileViewSubscribed, null)

    override val currentUserImageUri: StateFlow<Uri?> = userInfo.map {
        it?.getPhotoUrl()
    }.stateIn(applicationScope, WhileViewSubscribed, null)

    override val isUserSignedIn: StateFlow<Boolean> = userInfo.map {
        it?.isSignedIn() ?: false
    }.stateIn(applicationScope, WhileViewSubscribed, false)

    override val isUserRegistered: StateFlow<Boolean> = userInfo.map {
        it?.isRegistered() ?: false
    }.stateIn(applicationScope, WhileViewSubscribed, false)

    init {
        applicationScope.launch {
            userInfo.collect {
                if (notificationsPrefIsShownUseCase(Unit).data == false && isUserSignedInValue) {
                    _signInNavigationActions.tryOffer(SignInNavigationAction.ShowNotificationPreferencesDialog)
                }
            }
        }
    }

    override val showReservations: StateFlow<Boolean> = userInfo.map {
        (isUserRegisteredValue || !isUserSignedInValue) &&
                isReservationEnabledByRemoteConfig
    }.stateIn(applicationScope, WhileViewSubscribed, false)

    override suspend fun emitSignInRequest(): Unit = withContext(ioDispatcher) {
        // Refresh the notificationsPrefIsShown because it's used to indicate if the
        // notifications preference dialog should be shown
        notificationsPrefIsShownUseCase(Unit)
        _signInNavigationActions.tryOffer(RequestSignIn)
    }

    override suspend fun emitSignOutRequest(): Unit = withContext(mainDispatcher) {
        _signInNavigationActions.tryOffer(RequestSignOut)
    }

    override val isUserSignedInValue: Boolean
        get() = isUserSignedIn.value

    override val isUserRegisteredValue: Boolean
        get() = isUserRegistered.value

    override val userIdValue: String?
        get() = userInfo.value?.getUid()

    override val userId: Flow<String?>
        get() = userInfo.mapLatest { it?.getUid() }
            .stateIn(applicationScope, WhileViewSubscribed, null)

}