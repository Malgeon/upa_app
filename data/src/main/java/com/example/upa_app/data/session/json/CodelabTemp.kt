package com.example.upa_app.data.session.json

/** Like `Codelab` but with a list of IDs instead of tags. */
data class CodelabTemp(
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
    /** Names of Tags applicable to this codelab */
    val tagNames: List<String>,
    /** Sort priorty. Higher sort priority should come before lower ones. */
    val sortPriority: Int
)
