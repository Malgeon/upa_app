package com.example.upa_app.shared.util

import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.os.ParcelCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.SendChannel
import org.threeten.bp.ZonedDateTime
import timber.log.Timber

/** Convenience for callbacks/listeners whose return value indicates an event was consumed. */
inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

/**
 * Allows calls like
 *
 * `viewGroup.inflate(R.layout.foo)`
 */
fun ViewGroup.inflate(@LayoutRes layout: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layout, this, attachToRoot)
}

/**
 * Allows calls like
 *
 * `supportFragmentManager.inTransaction { add(...) }`
 */
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

// region Parcelables, Bundles

/** Write a boolean to a Parcel. */
fun Parcel.writeBooleanUsingCompat(value: Boolean) = ParcelCompat.writeBoolean(this, value)

/** Read a boolean from a Parcel. */
fun Parcel.readBooleanUsingCompat() = ParcelCompat.readBoolean(this)

// endregion
// region LiveData

/** Uses `Transformations.map` on a LiveData */
fun <X, Y> LiveData<X>.map(body: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, body)
}

fun <T> MutableLiveData<T>.setValueIfNew(newValue: T) {
    if (this.value != newValue) value = newValue
}

// endregion

// region ZonedDateTime
fun ZonedDateTime.toEpochMilli() = this.toInstant().toEpochMilli()
// endregion

/**
 * Helper to force a when statement to assert all options are matched in a when statement.
 *
 * By default, Kotlin doesn't care if all branches are handled in a when statement. However, if you
 * use the when statement as an expression (with a value) it will force all cases to be handled.
 *
 * This helper is to make a lightweight way to say you meant to match all of them.
 *
 * Usage:
 *
 * ```
 * when(sealedObject) {
 *     is OneType -> //
 *     is AnotherType -> //
 * }.checkAllMatched
 */
val <T> T.checkAllMatched: T
    get() = this

// region Coroutines

/**
 * Cancel the Job if it's active.
 */
fun Job?.cancelIfActive() {
    if (this?.isActive == true) {
        cancel()
    }
}

/**
 * Tries to send an element to a Channel and ignores the exception.
 */
fun <E> SendChannel<E>.tryOffer(element: E): Boolean = try {
    offer(element)
} catch (t: Throwable) {
    false // Ignore
}

// endregion

// region UI utils

// endregion

// region Firebase
//suspend fun <T> Task<T>.suspendAndWait(): T =
//    suspendCancellableCoroutine { continuation ->
//        addOnSuccessListener { result ->
//            continuation.resume(result)
//        }
//        addOnFailureListener { exception ->
//            continuation.resumeWithException(exception)
//        }
//        addOnCanceledListener {
//            continuation.resumeWithException(Exception("Firebase Task was cancelled"))
//        }
//    }
// endregion

/**
 * Helper to throw exceptions only in Debug builds, logging a warning otherwise.
 */
//fun exceptionInDebug(t: Throwable) {
//    if (BuildConfig.DEBUG) {
//        throw t
//    } else {
//        Timber.e(t)
//    }
//}
