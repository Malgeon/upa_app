package com.example.upa_app.presentation.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.upa_app.domain.RefreshConferenceDataUseCase
import com.example.upa_app.domain.fcm.TopicSubscriber
import com.example.upa_app.domain.sessions.ConferenceDayIndexer
import com.example.upa_app.presentation.signin.SignInViewModelDelegate
import com.example.upa_app.shared.util.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.ZoneId
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val loadScheduleUserSessionsUseCase: LoadScheduleUserSessionsUseCase,
    signInViewModelDelegate: SignInViewModelDelegate,
    scheduleUiHintsShownUseCase: ScheduleUiHintsShownUseCase,
    topicSubscriber: TopicSubscriber,
    private val snackbarMessageManager: SnackbarMessageManager,
    getTimeZoneUseCase: GetTimeZoneUseCase,
    private val refreshConferenceDataUseCase: RefreshConferenceDataUseCase,
    observeConferenceDataUseCase: ObserveConferenceDataUseCase
) : ViewModel(),
    SignInViewModelDelegate by signInViewModelDelegate {

    // Exposed to the view as a StateFlow but it's a one-shot operation
    val timeZoneId = flow<ZoneId> {
        if (getTimeZoneUseCase(Unit).successOr(true)) {
            emit(TimeUtils.CONFERENCE_TIMEZONE)
        } else {
            emit(ZoneId.systemDefault())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, TimeUtils.CONFERENCE_TIMEZONE)

    val isConferenceTimeZone: StateFlow<Boolean> = timeZoneId.mapLatest { zoneId ->
        TimeUtils.isConferenceTimeZone(zoneId)
    }.stateIn(viewModelScope, SharingStarted.Lazily, true)

    private lateinit var dayIndexer: ConferenceDayIndexer

    // Used to re-run flows on command
    private val refreshSignal = MutableSharedFlow<Unit>()

    // Used to run flows on init and also on command
    private val loadDataSignal: Flow<Unit> = flow {
        emit(Unit)
        emitAll(refreshSignal)
    }

    // Event coming from repository indicating data should be refreshed
    init {
        viewModelScope.launch {
            observeConferenceDataUseCase(Unit).collect {
                Timber.e("observing")
                refreshUserSessions()
            }
        }
    }

    // Latest user ID
    private val currentUserId = userId.stateIn(viewModelScope, WhileViewSubscribed, null)

    // Refresh sessions when needed and when the user changes
    private val loadSessionsResult: StateFlow<Result<LoadScheduleUserSessionsResult>> =
        loadDataSignal.combineTransform(currentUserId) { _, userId ->
            emitAll(
                loadScheduleUserSessionsUseCase(
                    LoadScheduleUserSessionsParameters(userId)
                )
            )
        }
            .onEach {
                // Side effect: show error messages coming from LoadScheduleUserSessionsUseCase
                if (it is Error) {
                    _errorMessage.tryOffer(it.exception.message ?: "Error")
                }
                // Side effect: show snackbar if the result contains a message
                if (it is Success) {
                    it.data.userMessage?.type?.stringRes()?.let { messageId ->
                        // There is a message to display:
                        snackbarMessageManager.addMessage(
                            SnackbarMessage(
                                messageId = messageId,
                                longDuration = true,
                                session = it.data.userMessageSession,
                                requestChangeId = it.data.userMessage?.changeRequestId
                            )
                        )
                    }
                }
            }
            .stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    val isLoading: StateFlow<Boolean> = loadSessionsResult.mapLatest {
        it == Result.Loading
    }.stateIn(viewModelScope, WhileViewSubscribed, true)

    // Expose new UI data when loadSessionsResult changes
    val scheduleUiData: StateFlow<ScheduleUiData> =
        loadSessionsResult.combineTransform(timeZoneId) { sessions, timeZone ->
            sessions.data?.let { data ->
                dayIndexer = data.dayIndexer
                emit(
                    ScheduleUiData(
                        list = data.userSessions,
                        dayIndexer = data.dayIndexer,
                        timeZoneId = timeZone
                    )
                )
            }
        }.stateIn(viewModelScope, WhileViewSubscribed, ScheduleUiData())

    private val _swipeRefreshing = MutableStateFlow(false)
    val swipeRefreshing: StateFlow<Boolean> = _swipeRefreshing

    /** Flows for Actions and Events **/

    // SIDE EFFECTS: Error messages
    // Guard against too many error messages by limiting to 3, keeping the oldest.
    private val _errorMessage = Channel<String>(1, BufferOverflow.DROP_LATEST)
    val errorMessage: Flow<String> =
        _errorMessage.receiveAsFlow().shareIn(viewModelScope, WhileViewSubscribed)

    // SIDE EFFECTS: Navigation actions
    private val _navigationActions = Channel<ScheduleNavigationAction>(capacity = Channel.CONFLATED)

    // Exposed with receiveAsFlow to make sure that only one observer receives updates.
    val navigationActions = _navigationActions.receiveAsFlow()

    /** Show hint for the schedule if they haven't been shown yet */
    init {
        viewModelScope.launch {
            scheduleUiHintsShownUseCase(Unit).successOr(false).let { scheduleHintsShown ->
                if (!scheduleHintsShown) {
                    _navigationActions.tryOffer(ShowScheduleUiHints)
                }
            }
        }
    }

    // Flags used to indicate if the "scroll to now" feature has been used already.
    var userHasInteracted = false

    // Flow describing which item to scroll to automatically.
    // Using a MutableSharedFlow so a new value can be emitted from a user event and so
    // the values are not replayed.
    private val currentEventIndex = MutableSharedFlow<Int>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val scrollToEvent: SharedFlow<ScheduleScrollEvent> =
        loadSessionsResult.combineTransform(currentEventIndex) { result, currentEventIndex ->
            if (userHasInteracted) {
                // Setting smoothScroll to false as it's an unnecessary delay.
                emit(ScheduleScrollEvent(currentEventIndex, smoothScroll = false))
            } else {
                val index = result.data?.firstUnfinishedSessionIndex ?: return@combineTransform
                if (index != -1) {
                    // User hasn't interacted yet and conference is happening
                    emit(ScheduleScrollEvent(index))
                } else {
                    // User hasn't interacted but conference not in progress, scroll to first event
                    emit(ScheduleScrollEvent(currentEventIndex))
                }
            }
        }.shareIn(viewModelScope, WhileViewSubscribed, replay = 0) // Don't replay on rotation

    init {
        // Subscribe user to schedule updates
        topicSubscriber.subscribeToScheduleUpdates()
    }

    fun onSwipeRefresh() {
        viewModelScope.launch {
            // Ask repository to fetch new data
            _swipeRefreshing.emit(true)
            refreshConferenceDataUseCase(Any())
            _swipeRefreshing.emit(false)
        }
    }

    private fun refreshUserSessions() {
        refreshSignal.tryEmit(Unit)
    }

    fun scrollToStartOfDay(day: ConferenceDay) {
        currentEventIndex.tryEmit(dayIndexer.positionForDay(day))
    }
}

data class ScheduleUiData(
    val list: List<UserSession>? = null,
    val timeZoneId: ZoneId? = null,
    val dayIndexer: ConferenceDayIndexer? = null
)

data class ScheduleScrollEvent(val targetPosition: Int, val smoothScroll: Boolean = false)