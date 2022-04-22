package com.example.upa_app.data

import com.example.upa_app.model.ConferenceData

object BootstrapConferenceDataSource : ConferenceDataSource {

    override fun getRemoteConferenceData(): ConferenceData? {
        throw Exception("Bootstrap data source doesn't have remote data")
    }

    override fun getOfflineConferenceData(): ConferenceData? {
        return loadAndParseBootstrapData()
    }

    fun loadAndParseBootstrapData(): ConferenceData {

        val conferenceDataStream = this.javaClass.classLoader!!
            .getResource(BuildConfig.BOOTSTRAP_CONF_DATA_FILENAME).openStream()

        return ConferenceDataJsonParser.parseConferenceData(conferenceDataStream)
    }
}