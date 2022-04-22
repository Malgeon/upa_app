package com.example.upa_app.model

data class Codelab(
    /** Unique ID identifying this Codelab */
    val id: String,
    /** Codelab title */
    val title: String,
    /** A short description of the codelab content */
    val description: String,
    /** Approximate time in minutes a user would spend doing this codelab */
    val durationMinutes: Int,
    /** URL for an icon to display */
    val iconUrl: String?,
    /** URL to access this codelab on the web */
    val codelabUrl: String,
    /** Sort priorty. Higher sort priority should come before lower ones. */
    val sortPriority: Int,
    /** [Tag]s applicable to this codelab */
    val tags: List<Tag>
) {
    fun hasUrl() = codelabUrl.isNotBlank()
}