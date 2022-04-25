package com.example.upa_app.presentation.photo

import androidx.lifecycle.ViewModel
import com.example.upa_app.model.SessionId
import com.example.upa_app.presentation.sessioncommon.OnSessionClickListener
import com.example.upa_app.presentation.sessioncommon.OnSessionStarClickDelegate
import com.example.upa_app.shared.util.tryOffer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

// Note: clients should obtain this from the Activity
@HiltViewModel
class PhotoTwoPaneViewModel @Inject constructor(
    onSessionStarClickDelegate: OnSessionStarClickDelegate
) : ViewModel(),
    OnSessionClickListener,
    OnSessionStarClickDelegate by onSessionStarClickDelegate {

    private val _isTwoPane = MutableStateFlow(false)
    val isTwoPane: StateFlow<Boolean> = _isTwoPane

    private val _returnToListPaneEvents = Channel<Unit>(capacity = Channel.CONFLATED)
    val returnToListPaneEvents = _returnToListPaneEvents.receiveAsFlow()

    private val _selectSessionEvents = Channel<SessionId>(capacity = Channel.CONFLATED)
    val selectSessionEvents = _selectSessionEvents

    fun setIsTwoPane(isTwoPane: Boolean) {
        _isTwoPane.value = isTwoPane
    }

    fun returnToListPane() {
        _returnToListPaneEvents.tryOffer(Unit)
    }

    override fun openEventDetail(id: SessionId) {
        _selectSessionEvents.tryOffer(id)
    }

}