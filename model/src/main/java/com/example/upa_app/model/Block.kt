package com.example.upa_app.model

import org.threeten.bp.ZonedDateTime

/**
 * Defines a block of time associated with the conference. For example, a span of time denotes the
 * time when codelabs are offered, or when lunch is provided, etc.
 */
data class Block(
    /**
     * The title of the block. Example, "Sandbox".
     */
    val title: String,

    /**
     * The type of agenda item. Example, "concert", or "meal".
     */
    val type: String,

    /**
     * The color of the block.
     */
    val color: Int,

    /**
     * The stroke color of the block (defaults to [color])
     */
    val strokeColor: Int = color,

    /**
     * If [color] is dark i.e. overlaid text should be light (defaults to false)
     */
    val isDark: Boolean = false,

    /**
     * Start time
     */
    val startTime: ZonedDateTime,

    /**
     * End time
     */
    val endTime: ZonedDateTime
)
