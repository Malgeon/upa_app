package com.example.upa_app.presentation.util

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Map a slideOffset (in the range `[-1, 1]`) to an alpha value based on the desired range.
 * For example, `slideOffsetToAlpha(0.5, 0.25, 1) = 0.33` because 0.5 is 1/3 of the way between
 * 0.25 and 1. The result value is additionally clamped to the range `[0. 1]`.
 */
fun slideOffsetToAlpha(value: Float, rangeMin: Float, rangeMax: Float): Float {
    return ((value - rangeMin) / (rangeMax - rangeMin)).coerceIn(0f, 1f)
}

/**
 * Launches a new coroutine and repeats 'block' every time the Fragment's viewLifecycleOwner
 * is in and out of 'minActiveState' lifecycle state
 */
inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

/**
 * Set the maximum width the view should take as a percent of its parent. The view must a direct
 * child of a ConstraintLayout.
 */
fun setContentMaxWidth(view: View) {
    val parent = view.parent as? ConstraintLayout ?: return
    val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
    val screenDensity = view.resources.displayMetrics.density
    val widthDp = parent.width / screenDensity
    val widthPercent = getContextMaxWidthPercent(widthDp.toInt())
    layoutParams.matchConstraintPercentWidth = widthPercent
    view.requestLayout()
}


private fun getContextMaxWidthPercent(maxWidthDp: Int): Float {
    // These match @dimen/content_max_width_percent.
    return when {
        maxWidthDp >= 1024 -> 0.6f
        maxWidthDp >= 840 -> 0.7f
        maxWidthDp >= 600 -> 0.8f
        else -> 1f
    }
}