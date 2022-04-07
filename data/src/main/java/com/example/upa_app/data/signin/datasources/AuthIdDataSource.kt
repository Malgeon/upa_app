package com.example.upa_app.data.signin.datasources

/**
 * Interface to simply get the current authenticated user ID.
 */
interface AuthIdDataSource {
    fun getUserId(): String?
}
