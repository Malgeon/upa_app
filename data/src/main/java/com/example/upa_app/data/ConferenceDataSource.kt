package com.example.upa_app.data

import com.example.upa_app.model.ConferenceData

interface ConferenceDataSource {
    fun getRemoteConferenceData(): ConferenceData?
    fun getOfflineConferenceData(): ConferenceData?
}

enum class UpdateSource {
    NONE,
    NETWORK,
    CACHE,
    BOOTSTRAP,
    LOCAL
}