package com.example.upa_app.presentation.messages

import com.example.upa_app.model.Session

data class SnackbarMessage(
    /** Resource string ID of the message to show */
    val messageId: Int,

    /** Optional resource string ID for the action (example: "Got it!") */
    val actionId: Int? = null,

    /** Set to true for a Snackbar with long duration */
    val longDuration: Boolean = false,

    /** Optional change ID to avoid repetition of messages */
    val requestChangeId: String? = null,

    /** Optional session */
    val session: Session? = null
) {
    override fun toString(): String {
        return "Session: ${session?.id}, ${session?.title?.take(30)}. Change: $requestChangeId "
    }
}