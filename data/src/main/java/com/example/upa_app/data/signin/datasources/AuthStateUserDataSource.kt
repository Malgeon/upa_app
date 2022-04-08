package com.example.upa_app.data.signin.datasources

import com.example.upa_app.data.signin.AuthenticatedUserInfoBasic
import kotlinx.coroutines.flow.Flow

/**
 * Listens to an Authentication state data source that emits updates on the current user.
 *
 * @see FirebaseAuthStateUserDataSource
 */
interface AuthStateUserDataSource {
    /**
     * Returns an observable of the [AuthenticatedUserInfoBasic].
     */
    fun getBasicUserInfo(): Flow<Result<AuthenticatedUserInfoBasic?>>
}