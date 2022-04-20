package com.example.upa_app.model.reservations

/**
* Entity that represents the client's latest reservation or cancellation request. Used to figure
* out whether a reservation request is pending or completed.
*/
data class ReservationRequest(
    /**
     * The action of the reservation request (REQUEST/CANCEL).
     */
    val action: ReservationRequestEntityAction,

    /**
     * An ID set by the client that will be added to the reservation result on completion.
     */
    val requestId: String
) {

    enum class ReservationRequestEntityAction {
        /** The reservation was granted */
        RESERVE_REQUESTED,

        /** The reservation was granted but the user was placed on a waitlist. */
        CANCEL_REQUESTED;

        companion object {

            fun getIfPresent(string: String): ReservationRequestEntityAction? {
                return try {
                    valueOf(string)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }
    }
}
