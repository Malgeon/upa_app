package com.example.upa_app.domain.component.notifications

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.example.upa_app.data.pref.PreferenceStorage
import com.example.upa_app.data.signin.datasources.AuthIdDataSource
import com.example.upa_app.domain.sessions.LoadSessionOneShotUseCase
import com.example.upa_app.domain.sessions.LoadUserSessionOneShotUseCase
import com.example.upa_app.model.Session
import com.example.upa_app.model.userdata.UserSession
import com.example.upa_app.shared.di.ApplicationScope
import com.example.upa_app.shared.R
import com.example.upa_app.shared.result.Result
import com.example.upa_app.shared.result.Result.Success
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import org.threeten.bp.Instant
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Receives broadcast intents with information for session notifications.
 */
@AndroidEntryPoint
class AlarmBroadcastReceiver : BroadcastReceiver()  {

    @Inject
    lateinit var preferencesStorage: PreferenceStorage

    @Inject
    lateinit var loadUserSession: LoadUserSessionOneShotUseCase

    @Inject
    lateinit var loadSession: LoadSessionOneShotUseCase

    @Inject
    lateinit var alarmManager: SessionAlarmManager

    @Inject
    lateinit var authIdDataSource: AuthIdDataSource

    @ApplicationScope
    @Inject
    lateinit var externalScope: CoroutineScope


    override fun onReceive(p0: Context?, p1: Intent?) {
        TODO("Not yet implemented")
    }

    @WorkerThread
    private suspend fun checkThenShowPreSessionNotification(
        context: Context,
        sessionId: String,
        userId: String
    ) {
        // TODO use preferToReceiveNotifications as flow
        if (!preferencesStorage.preferToReceiveNotifications.first()) {
            Timber.d("User disabled notifications, not showing")
            return
        }

        Timber.d("Showing pre session notification for $sessionId, user $userId")

        val userEvent: Result<UserSession>? = getUserEvent(userId, sessionId)
        // Don't notify if for some reason the event is no longer starred or reserved.
        if (userEvent is Success) {
            val event = userEvent.data.userEvent
            if (event.isPreSessionNotificationRequired() &&
                isSessionCurrent(userEvent.data.session)
            ) {
                try {
                    val notificationId = showPreSessionNotification(context, userEvent.data.session)
                    // Dismiss in any case
                    alarmManager.dismissNotificationInFiveMinutes(notificationId)
                } catch (ex: Exception) {
                    Timber.e(ex)
                    return
                }
            }
        } else {
            // There was no way to get UserEvent info, notify in case of connectivity error.
            notifyWithoutUserEvent(sessionId, context)
        }
    }

    private suspend fun notifyWithoutUserEvent(sessionId: String, context: Context) {
        return try {
            // Using coroutineScope to propagate exception to the try/catch block
            coroutineScope {
                // Using async coroutine builder to wait for the result of the use case computation
                val session = async { loadSession(sessionId) }.await()
                if (session is Success) {
                    val notificationId = showPreSessionNotification(context, session.data)
                    alarmManager.dismissNotificationInFiveMinutes(notificationId)
                } else {
                    Timber.e("Session couldn't be loaded for notification")
                }
            }
        } catch (ex: Exception) {
            Timber.e("Exception loading session for notification: ${ex.message}")
        }
    }

    private suspend fun getUserEvent(userId: String, sessionId: String): Result<UserSession>? {
        return try {
            // Using coroutineScope to propagate exception to the try/catch block
            coroutineScope {
                // Using async coroutine builder to wait for the result of the use case computation
                async { loadUserSession(userId to sessionId) }.await()
            }
        } catch (ex: Exception) {
            Timber.e(
                """Session notification is set, however there was an error confirming
                    |that the event is still starred. Showing notification""".trimMargin()
            )
            null
        }
    }

    @WorkerThread
    private fun showPreSessionNotification(context: Context, session: Session): Int {
        val notificationManager: NotificationManager = context.getSystemService()
            ?: throw Exception("Notification Manager not found.")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannelForPreSession(context, notificationManager)
        }

        val intent = Intent(
            Intent.ACTION_VIEW,
            "iosched://sessions?$QUERY_SESSION_ID=${session.id}".toUri()
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context)
            // Add the intent, which inflates the back stack
            .addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_UPCOMING)
            .setContentTitle(session.title)
            .setContentText(context.getString(R.string.starting_soon))
            .setSmallIcon(R.drawable.ic_notification_io_logo)
            .setContentIntent(resultPendingIntent)
            .setTimeoutAfter(TimeUnit.MINUTES.toMillis(10)) // Backup (cancelled with receiver)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val notificationId = session.id.hashCode()
        notificationManager.notify(notificationId, notification)
        return notificationId
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeNotificationChannelForPreSession(
        context: Context,
        notificationManager: NotificationManager
    ) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID_UPCOMING,
                context.getString(R.string.session_notifications),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { lockscreenVisibility = Notification.VISIBILITY_PUBLIC }
        )
    }

    private fun isSessionCurrent(session: Session): Boolean {
        return session.startTime.toInstant().isAfter(Instant.now())
    }

    companion object {
        const val EXTRA_NOTIFICATION_CHANNEL = "notification_channel"
        const val EXTRA_SESSION_ID = "user_event_extra"

        /** If this flag it set to true in session detail, the show rate dialog should be opened */
        const val EXTRA_SHOW_RATE_SESSION_FLAG = "show_rate_session_extra"

        const val QUERY_SESSION_ID = "session_id"
        const val CHANNEL_ID_UPCOMING = "upcoming_channel_id"
        const val CHANNEL_ID_FEEDBACK = "feedback_channel_id"
    }
}