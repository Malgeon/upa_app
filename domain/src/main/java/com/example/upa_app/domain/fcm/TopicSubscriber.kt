package com.example.upa_app.domain.fcm

interface TopicSubscriber {
    /**
     * Used to subscribe all users that open the schedule screen to a topic, to receive future
     * updates.
     */
    fun subscribeToScheduleUpdates()

    /**
     * If a user is registered subscribe them to the "registered" topic.
     */
    fun subscribeToAttendeeUpdates()

    /**
     * If a user is registered and signs out, unsubscribe them from the "registered" topic to stop
     * receiving notifications.
     */
    fun unsubscribeFromAttendeeUpdates()
}