package com.example.upa_app.domain.component.notifications

import android.app.AlarmManager
import android.app.AlarmManager.RTC
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.getSystemService
import com.example.upa_app.model.Session
import com.example.upa_app.model.SessionId
import com.example.upa_app.model.userdata.UserSession
import com.example.upa_app.shared.util.toEpochMilli
import dagger.hilt.android.qualifiers.ApplicationContext
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


open class SessionAlarmManager @Inject constructor(@ApplicationContext val context: Context) {

    private val systemAlarmManager: AlarmManager? = context.getSystemService()

    /**
     * Schedules an alarm for a session.
     */
    fun setAlarmForSession(userSession: UserSession) {
        val session = userSession.session
        if ((session.startTime.toInstant().minusMillis(alarmTimeDelta)).isBefore(Instant.now())) {
            Timber.d("Trying to schedule an alarm for a past session, ignoring.")
            return
        }

        cancelAlarmForSession(session.id)

        val upcomingIntent =
            makePendingIntent(session.id, AlarmBroadcastReceiver.CHANNEL_ID_UPCOMING)
        upcomingIntent?.let {
            scheduleAlarmForPreSession(it, session)
        }
        if (userSession.isPostSessionNotificationRequired()) {
            val feedbackIntent =
                makePendingIntent(session.id, AlarmBroadcastReceiver.CHANNEL_ID_FEEDBACK)
            feedbackIntent?.let {
                scheduleAlarmForPostSession(it, session)
            }
        }
    }

    open fun cancelAlarmForSession(sessionId: SessionId) {
        val upcomingIntent =
            makePendingIntent(sessionId, AlarmBroadcastReceiver.CHANNEL_ID_UPCOMING)
        upcomingIntent?.let {
            cancelAlarmFor(it)
            Timber.d("Cancelled upcoming alarm for session $sessionId")
        }
        val feedbackIntent =
            makePendingIntent(sessionId, AlarmBroadcastReceiver.CHANNEL_ID_FEEDBACK)
        feedbackIntent?.let {
            cancelAlarmFor(it)
            Timber.d("Cancelled feedback alarm for session $sessionId")
        }
    }

    private fun makePendingIntent(sessionId: SessionId, channel: String): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            // To make the requestCode unique for the upcoming and feedback channels for the
            // same session, concatenating the strings
            (sessionId + channel).hashCode(),
            Intent(context, AlarmBroadcastReceiver::class.java)
                .putExtra(AlarmBroadcastReceiver.EXTRA_SESSION_ID, sessionId)
                .putExtra(
                    AlarmBroadcastReceiver.EXTRA_NOTIFICATION_CHANNEL,
                    channel
                ),
            FLAG_UPDATE_CURRENT
        )
    }

    private fun cancelAlarmFor(pendingIntent: PendingIntent) {
        try {
            systemAlarmManager?.cancel(pendingIntent)
        } catch (ex: Exception) {
            Timber.e("Couldn't cancel alarm for session")
        }
    }

    private fun scheduleAlarmForPreSession(pendingIntent: PendingIntent, session: Session) {
        val triggerAtMillis = session.startTime.toEpochMilli() - alarmTimeDelta
        scheduleAlarmFor(
            pendingIntent, session, triggerAtMillis,
            AlarmBroadcastReceiver.CHANNEL_ID_UPCOMING
        )
    }

    private fun scheduleAlarmForPostSession(pendingIntent: PendingIntent, session: Session) {
        val triggerAtMillis = session.endTime.toEpochMilli() + alarmTimeDelta
        scheduleAlarmFor(
            pendingIntent, session, triggerAtMillis,
            AlarmBroadcastReceiver.CHANNEL_ID_FEEDBACK
        )
    }

    private fun scheduleAlarmFor(
        pendingIntent: PendingIntent,
        session: Session,
        triggerAtMillis: Long,
        channel: String
    ) {
        systemAlarmManager?.let {
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                systemAlarmManager,
                RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
            Timber.d(
                """Schedule aram for session ${session.title} at $triggerAtMillis
                  |for channel: $channel""".trimMargin()
            )
        }
    }

    fun dismissNotificationInFiveMinutes(notificationId: Int) {
        systemAlarmManager?.let {
            val intent = Intent(context, CancelNotificationBroadcastReceiver::class.java)
            intent.putExtra(
                CancelNotificationBroadcastReceiver.NOTIFICATION_ID_EXTRA, notificationId
            )
            val pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0)
            val triggerAtMillis = Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli()
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                systemAlarmManager,
                RTC,
                triggerAtMillis,
                pendingIntent
            )
            Timber.d("Scheduled notification dismissal for $notificationId at $triggerAtMillis")
        }
    }

    companion object {
        private val alarmTimeDelta = TimeUnit.MINUTES.toMillis(5)
    }
}