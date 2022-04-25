package com.example.upa_app.util

import android.app.Activity
import com.example.upa_app.data.pref.DataStorePreferenceStorage.PreferencesKeys.PREF_CODELABS_INFO_SHOWN
import com.example.upa_app.data.pref.DataStorePreferenceStorage.PreferencesKeys.PREF_CONFERENCE_TIME_ZONE
import com.example.upa_app.data.pref.DataStorePreferenceStorage.PreferencesKeys.PREF_MY_LOCATION_OPTED_IN
import com.example.upa_app.data.pref.DataStorePreferenceStorage.PreferencesKeys.PREF_NOTIFICATIONS_SHOWN
import com.example.upa_app.data.pref.DataStorePreferenceStorage.PreferencesKeys.PREF_ONBOARDING
import com.example.upa_app.data.pref.DataStorePreferenceStorage.PreferencesKeys.PREF_RECEIVE_NOTIFICATIONS
import com.example.upa_app.data.pref.DataStorePreferenceStorage.PreferencesKeys.PREF_SEND_USAGE_STATISTICS
import com.example.upa_app.data.pref.PreferenceStorage
import com.example.upa_app.presentation.signin.SignInViewModelDelegate
import com.example.upa_app.shared.analytics.AnalyticsActions
import com.example.upa_app.shared.analytics.AnalyticsHelper
import com.example.upa_app.shared.di.ApplicationScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * Firebase Analytics implementation of AnalyticsHelper
 */
class FirebaseAnalyticsHelper(
    @ApplicationScope private val externalScope: CoroutineScope,
    signInViewModelDelegate: SignInViewModelDelegate,
    private val preferenceStorage: PreferenceStorage
) : AnalyticsHelper {

    private var firebaseAnalytics = Firebase.analytics

    private var analyticsEnabled: Flow<Boolean> = preferenceStorage.sendUsageStatistics

    /**
     * Initialize Analytics tracker.  If the user has permitted tracking and has already signed TOS,
     * (possible except on first run), initialize analytics Immediately.
     */
    init {
        externalScope.launch { // Prevent access to preferences on main thread
            analyticsEnabled.collect {
                Timber.d("Setting Analytics enabled: $it")
                firebaseAnalytics.setAnalyticsCollectionEnabled(it)
            }
            // The listener will initialize Analytics when the TOS is signed, or enable/disable
            // Analytics based on the "anonymous data collection" setting.
            logSendUsageStatsFlagChanges()
        }

        externalScope.launch {
            // The listener will initialize Analytics when the TOS is signed, or enable/disable
            // Analytics based on the "anonymous data collection" setting.
            logSendUsageStatsFlagChanges()
        }

        externalScope.launch {
            signInViewModelDelegate.isUserSignedIn.collect { signedIn ->
                setUserSignedIn(signedIn)
                Timber.d("Updated user signed in to $signedIn")
            }
        }
        externalScope.launch {
            signInViewModelDelegate.isUserRegistered.collect { registered ->
                setUserRegistered(registered)
                Timber.d("Updated user registered to $registered")
            }
        }
    }

    override fun sendScreenView(screenName: String, activity: Activity) {
        firebaseAnalytics.run {
            setCurrentScreen(activity, screenName, null)
            logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.ITEM_ID, screenName)
                param(FirebaseAnalytics.Param.CONTENT_TYPE, FA_CONTENT_TYPE_SCREENVIEW)
            }
            Timber.d("Screen View recorded: $screenName")
        }
    }

    override fun logUiEvent(itemId: String, action: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, itemId)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, FA_CONTENT_TYPE_UI_EVENT)
            param(FA_KEY_UI_ACTION, action)
        }
        Timber.d("Event recorded for $itemId, $action")
    }

    override fun setUserSignedIn(isSignedIn: Boolean) {
        // todo : Set up user properties in both dev and prod
        firebaseAnalytics.setUserProperty(UPROP_USER_SIGNED_IN, isSignedIn.toString())
    }

    override fun setUserRegistered(isRegistered: Boolean) {
        // todo : Set up user properties in both dev and prod
        firebaseAnalytics.setUserProperty(UPROP_USER_REGISTERED, isRegistered.toString())
    }

    /**
     * Set up a listener for preference changes.
     */
    private suspend fun logSendUsageStatsFlagChanges() {
        // not logged: showScheduleUiHints, selectedFilters, selectedTheme
        flowOf(
            preferenceStorage.codelabsInfoShown.map { PREF_CODELABS_INFO_SHOWN.name to it },
            preferenceStorage.myLocationOptedIn.map { PREF_MY_LOCATION_OPTED_IN.name to it },
            preferenceStorage.notificationsPreferenceShown.map {
                PREF_NOTIFICATIONS_SHOWN.name to it
            },
            preferenceStorage.onboardingCompleted.map { PREF_ONBOARDING.name to it },
            preferenceStorage.preferConferenceTimeZone.map { PREF_CONFERENCE_TIME_ZONE.name to it },
            preferenceStorage.preferToReceiveNotifications.map {
                PREF_RECEIVE_NOTIFICATIONS.name to it
            },
            preferenceStorage.sendUsageStatistics.map { PREF_SEND_USAGE_STATISTICS.name to it }
        ).flattenMerge().collect { (key, value) ->
            val action = if (value) AnalyticsActions.ENABLE else AnalyticsActions.DISABLE
            logUiEvent("Preference: $key", action)
        }
    }

    companion object {
        private const val UPROP_USER_SIGNED_IN = "user_signed_in"
        private const val UPROP_USER_REGISTERED = "user_registered"

        /**
         * Log a specific screen view under the `screenName` string.
         */
        private const val FA_CONTENT_TYPE_SCREENVIEW = "screen"
        private const val FA_KEY_UI_ACTION = "ui_action"
        private const val FA_CONTENT_TYPE_UI_EVENT = "ui event"

    }
}