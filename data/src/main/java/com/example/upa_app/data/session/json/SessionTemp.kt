package com.example.upa_app.data.session.json

import com.example.upa_app.model.SessionId
import org.threeten.bp.ZonedDateTime

/**
 * Like `Session` but with list of IDs instead of objects in tags, speakers and related sessions.
 */
data class SessionTemp(
    /**
     * Unique string identifying this session.
     */
    val id: SessionId,

    /**
     * Start time of the session
     */
    val startTime: ZonedDateTime,

    /**
     * End time of the session
     */
    val endTime: ZonedDateTime,

    /**
     * Session title.
     */
    val title: String,

    /**
     * Body of text explaining this session in detail.
     */
    val description: String,

    /**
     * Full URL for the session online.
     */
    val sessionUrl: String,

    /**
     * The session room.
     */
    val room: String,

    /**
     * Indicates if the Session has a live stream.
     */
    val isLivestream: Boolean,

    /**
     * Full URL to YouTube.
     */
    val youTubeUrl: String,

    /**
     * URL to the Dory page.
     */
    val doryLink: String,

    /**
     * IDs of the `Tag`s associated with the session. Ordered, with the most important tags
     * appearing first.
     */
    val tagNames: List<String>,

    /**
     * IDs of the session speakers.
     */
    val speakers: Set<String>,

    /**
     * The session's photo URL.
     */
    val photoUrl: String,

    /**
     * IDs of the sessions related to this session.
     */
    val relatedSessions: Set<String>
)
