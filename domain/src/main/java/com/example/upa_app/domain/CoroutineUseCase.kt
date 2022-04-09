package com.example.upa_app.domain

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Executes business logic synchronously or asynchronously using Coroutines.
 */
abstract class UseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {

}