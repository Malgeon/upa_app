package com.example.upa_app.data.signin.datasources

import kotlinx.coroutines.flow.Flow

/**
 * A data source that listens to changes in the user data related to event
 * registration.
 */
interface RegisteredUserDataSource {
    /**
     * Returns the holder of the result of listening to the data source.
     */
    fun observeUserChanges(userId: String): Flow<Result<Boolean?>>
}