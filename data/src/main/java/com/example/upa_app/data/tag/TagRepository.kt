package com.example.upa_app.data.tag

import com.example.upa_app.data.ConferenceDataRepository
import com.example.upa_app.model.Tag
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single point of access to tag data for the presentation layer.
 */
@Singleton
open class TagRepository @Inject constructor(
    private val conferenceDataRepository: ConferenceDataRepository
) {
    fun getTags(): List<Tag> = conferenceDataRepository.getOfflineConferenceData().tags
}