package com.example.upa_app.domain.search

import androidx.core.os.trace
import com.example.upa_app.data.db.AppDatabase
import com.example.upa_app.model.userdata.UserSession
import javax.inject.Inject


interface SessionTextMatchStrategy {
    suspend fun searchSessions(userSessions: List<UserSession>, query: String): List<UserSession>
}

/** Searches sessions by simple string comparison against title and description. */
object SimpleMatchStrategy : SessionTextMatchStrategy {

    override suspend fun searchSessions(
        userSessions: List<UserSession>,
        query: String
    ): List<UserSession> {
        trace("search-path-simplematchstrategy") {
            if (query.isEmpty()) {
                return userSessions
            }
            val lowercaseQuery = query.toLowerCase()
            return userSessions.filter {
                it.session.title.toLowerCase().contains(lowercaseQuery) ||
                        it.session.description.toLowerCase().contains(lowercaseQuery)
            }
        }
    }
}

/** Searches sessions using FTS. */
class FtsMatchStrategy @Inject constructor(
    private val appDatabase: AppDatabase
) : SessionTextMatchStrategy {

    override suspend fun searchSessions(
        userSessions: List<UserSession>,
        query: String
    ): List<UserSession> {
        trace("search-path-ftsmatchstrategy") {
            if (query.isEmpty()) {
                return userSessions
            }
            val sessionIds = trace("search-path-roomquery") {
                appDatabase.sessionFtsDao().searchAllSessions(query.toLowerCase()).toSet()
            }
            return userSessions.filter { it.session.id in sessionIds }
        }
    }
}