package com.example.upa_app.shared.config

import com.example.upa_app.model.ConferenceWifiInfo

interface AppConfigDataSource {

    fun getTimestamp(key: String): String // TODO: change name
    /**
     * Sync the strings with the latest values with Remote Config
     */
    suspend fun syncStrings()
    fun getWifiInfo(): ConferenceWifiInfo
    fun isMapFeatureEnabled(): Boolean
    fun isExploreArFeatureEnabled(): Boolean
    fun isCodelabsFeatureEnabled(): Boolean
    fun isSearchScheduleFeatureEnabled(): Boolean
    fun isSearchUsingRoomFeatureEnabled(): Boolean
    fun isAssistantAppFeatureEnabled(): Boolean
    fun isReservationFeatureEnabled(): Boolean
    fun isFeedEnabled(): Boolean
}