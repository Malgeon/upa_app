package com.example.upa_app.model

import androidx.annotation.ColorInt
import org.threeten.bp.ZonedDateTime

/**
 * Describes an item in the feed, displaying social-media like updates.
 * Each item includes a message, id, a title and a timestamp.
 * Optionally, it can also include an image and a category with a color.
 * An item can also be marked as priority.
 */
data class Announcement(
    val id: String,
    val title: String,
    val message: String,
    val priority: Boolean,
    /**
     * Marks this feed item as an emergency.
     */
    val emergency: Boolean,
    val timestamp: ZonedDateTime,
    val imageUrl: String?,
    /**
     * Item category. Free form string.
     */
    val category: String,
    @ColorInt val color: Int
) {
    val hasImage = !imageUrl.isNullOrEmpty()
}
