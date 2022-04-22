package com.example.upa_app.model

import androidx.annotation.ColorInt
import org.threeten.bp.ZonedDateTime

data class Moment(
    val id: String,
    val title: String?,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val timeVisible: Boolean,
    @ColorInt val textColor: Int,
    val imageUrl: String,
    val imageUrlDarkTheme: String,
    val attendeeRequired: Boolean,
    val ctaType: String, // MAP_LOCATION, LIVE_STREAM, SIGN_IN, NO_ACTION
    val featureId: String?, // if ctaType is MapLocation
    val featureName: String?, // if ctaType is MapLocation
    val streamUrl: String? // if ctaType is LiveStream
) {
    companion object {
        const val CTA_LIVE_STREAM = "LIVE_STREAM"
        const val CTA_MAP_LOCATION = "MAP_LOCATION"
        const val CTA_SIGNIN = "SIGN_IN"
        const val CTA_NO_ACTION = "NO_ACTION"
    }
}