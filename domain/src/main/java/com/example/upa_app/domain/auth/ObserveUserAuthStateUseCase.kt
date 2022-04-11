package com.example.upa_app.domain.auth

import com.example.upa_app.data.signin.datasources.AuthStateUserDataSource
import com.example.upa_app.data.signin.datasources.RegisteredUserDataSource
import com.example.upa_app.domain.fcm.TopicSubscriber
import com.example.upa_app.shared.di.ApplicationScope
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
    @ApplicationScope

){
}