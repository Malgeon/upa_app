package com.example.upa_app.presentation.sessioncommon

import com.example.upa_app.model.SessionId
import com.example.upa_app.model.userdata.UserSession

/**
 * Actions that can be performed on events.
 */
interface OnSessionClickListener {
    fun openEventDetail(id: SessionId)
}

interface OnSessionStarClickListener {
    fun onStarClicked(userSession: UserSession)
}
