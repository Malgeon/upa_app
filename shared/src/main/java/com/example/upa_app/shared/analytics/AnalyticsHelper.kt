package com.example.upa_app.shared.analytics

import android.app.Activity


/** Analytics API surface */
interface AnalyticsHelper {
    /** Record a screen view */
    fun sendScreenView(screenName: String, activity: Activity)

    /** Record a UI event, e.g. user clicks a button */
    fun logUiEvent(itemId: String, action: String)

    /** Set the user signed in property */
    fun setUserSignedIn(isSignedIn: Boolean)

    /** Set the user registered property */
    fun setUserRegistered(isRegistered: Boolean)
}

/** Actions that should be used when sending analytics events */
object AnalyticsActions {
    // UI Actions
    const val STARRED = "Bookmarked"
    const val CLICK = "Clicked"
    const val RESERVE = "Reserved"
    const val RESERVE_FAILED = "Reserved (Failed)"
    const val RES_CANCEL_FAILED = "Reservation Cancellation (Failed)"
    const val RES_CANCEL = "Reservation Cancellation"

    const val MAP_MARKER_SELECT = "Selected Map Marker"
    const val MAP_MARKER_DETAILS = "Viewed Map Marker Details"
    const val WIFI_CONNECT = "Connected to Wifi"
    const val YOUTUBE_LINK = "Youtube link click"
    const val SEARCH_QUERY_SUBMIT = "Submitted search query"
    const val SEARCH_RESULT_CLICK = "Clicked on search result"

    const val HOME_TO_MAP = "Home to Map transition"
    const val HOME_TO_SCHEDULE = "Home to Schedule transition"
    const val HOME_TO_SESSION_DETAIL = "Home to Session Detail transition"
    const val HOME_TO_SIGN_IN = "Home to Sign In transition"
    const val HOME_TO_LIVESTREAM = "Home to Live stream transition"

    const val HOME_TO_ANNOUNCEMENTS = "Home to Announcements transition"

    // Settings Actions
    const val ENABLE = "Enabled"
    const val DISABLE = "Disabled"
}