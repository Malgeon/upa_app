package com.example.upa_app.presentation.reservation

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.upa_app.presentation.R

/**
 * An [AppCompatTextView] extension supporting multiple custom states, representing the status
 * of a user's reservation for an event.
 */
class ReservationTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var status = ReservationViewState.RESERVABLE
        set(value) {
            if (value == field) return
            field = value
            setText(value.text)
            refreshDrawableState()
        }

    init {
        setText(ReservationViewState.RESERVABLE.text)
        val drawable = context.getDrawable(R.drawable.asld_reservation)
        setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        @Suppress("SENSELESS_COMPARISON") // Status is null during super init
        if (status == null) return super.onCreateDrawableState(extraSpace)
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        mergeDrawableStates(drawableState, status.state)
        return drawableState
    }
}