package com.example.upa_app.domain.auth

import com.example.upa_app.data.signin.AuthenticatedUserInfo
import com.example.upa_app.data.signin.AuthenticatedUserInfoBasic
import com.example.upa_app.data.signin.FirebaseRegisteredUserInfo
import com.example.upa_app.data.signin.datasources.AuthStateUserDataSource
import com.example.upa_app.data.signin.datasources.RegisteredUserDataSource
import com.example.upa_app.domain.FlowUseCase
import com.example.upa_app.domain.fcm.TopicSubscriber
import com.example.upa_app.shared.di.ApplicationScope
import com.example.upa_app.shared.di.IoDispatcher
import com.example.upa_app.shared.result.Result
import com.example.upa_app.shared.result.Result.Success
import com.example.upa_app.shared.result.data
import com.example.upa_app.shared.util.cancelIfActive
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A [FlowUseCase] that observes two data sources to generate an [AuthenticatedUserInfo]
 * that includes whether the user is registered (is an attendee).
 *
 * [AuthStateUserDataSource] provides general user information, like user IDs, while
 * [RegisteredUserDataSource] observes a different data source to provide a flag indicating
 * whether the user is registered.
 */
@Singleton
open class ObserveUserAuthStateUseCase @Inject constructor(
    private val registeredUserDataSource: RegisteredUserDataSource,
    private val authStateUserDataSource: AuthStateUserDataSource,
    private val topicSubscriber: TopicSubscriber,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FlowUseCase<Any, AuthenticatedUserInfo>(ioDispatcher) {

    private var observeUserRegisteredChangeJob: Job? = null

    // As a separate coroutine needs to listen for user registration changes and emit to the
    // flow, a callbackFlow is used
    private val authStateChanges = callbackFlow<Result<AuthenticatedUserInfo>> {
        authStateUserDataSource.getBasicUserInfo().collect { userResult ->
            // Cancel observing previous user registered changes
            observeUserRegisteredChangeJob.cancelIfActive()

            if (userResult is Success) {
                val data = userResult.data
                if (data != null) {
                    processUserData(data)
                } else {
                    send(Success(FirebaseRegisteredUserInfo(null, false)))
                }
            } else {
                send(Result.Error(Exception("FirebaseAuth error")))
            }
        }

        // Always wait for the flow to be closed. Specially important for tests.
        awaitClose { observeUserRegisteredChangeJob.cancelIfActive() }
    }
        .shareIn(externalScope, SharingStarted.WhileSubscribed())

    override fun execute(parameters: Any): Flow<Result<AuthenticatedUserInfo>> {
        TODO("Not yet implemented")
    }

    private fun subscribeToRegisteredTopic() {
        topicSubscriber.subscribeToScheduleUpdates()
    }

    private fun unsubscribeFromRegisteredTopic() {
        topicSubscriber.unsubscribeFromAttendeeUpdates()
    }

    private suspend fun ProducerScope<Result<AuthenticatedUserInfo>>.processUserData(
        userData: AuthenticatedUserInfoBasic
    ) {
        if (!userData.isSignedIn()) {
            userSignedOut(userData)
        } else if (userData.getUid() != null) {
            userSignedIn(userData.getUid()!!, userData)
        } else {
            send(Success(FirebaseRegisteredUserInfo(userData, false)))
        }
    }

    private suspend fun ProducerScope<Result<AuthenticatedUserInfo>>.userSignedIn(
        userId: String,
        userData: AuthenticatedUserInfoBasic
    ) {
        // Observing the user registration changes from another scope to able to listen
        // for this and updates to getBasicUserInfo() simultaneously
        observeUserRegisteredChangeJob = externalScope.launch(ioDispatcher) {
            // Start observing the user in Firestore to fetch the 'registered' flag
            registeredUserDataSource.observeUserChanges(userId).collect { result ->
                val isRegisteredValue: Boolean? = result.data
                // When there's new user data and the user is an attendee, subscribe to topic:
                if (isRegisteredValue == true && userData.isSignedIn()) {
                    subscribeToRegisteredTopic()
                }

                send(Success(FirebaseRegisteredUserInfo(userData, isRegisteredValue)))
            }
        }
    }

    private suspend fun ProducerScope<Result<AuthenticatedUserInfo>>.userSignedOut(
        userData: AuthenticatedUserInfoBasic?
    ) {
        send(Success(FirebaseRegisteredUserInfo(userData, false)))
        unsubscribeFromRegisteredTopic() // Stop receiving notifications for attendees
    }
}