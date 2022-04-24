package com.example.upa_app.presentation.widget

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * [LinearSmoothScroller] implementation that first jumps closer to the target position if necessary
 * in order to avoid really long scrolling animation.
 */
class JumpSmoothScroller(
    context: Context,
    /** Maximum position difference to scroll normally without jumping first */
    private val maxDifference: Int = 5
) : LinearSmoothScroller(context) {

    override fun getVerticalSnapPreference() = SNAP_TO_START

    override fun getHorizontalSnapPreference() = SNAP_TO_START

    override fun onSeekTargetStep(dx: Int, dy: Int, state: RecyclerView.State, action: Action) {
        val layoutManager = layoutManager as? LinearLayoutManager
        if (layoutManager != null) {
            // If we're far enough away from the target position, jump closer before scrolling
            if (targetPosition + maxDifference < layoutManager.findFirstVisibleItemPosition()) {
                action.jumpTo(targetPosition + maxDifference)
                return
            }
            if (targetPosition - maxDifference > layoutManager.findLastVisibleItemPosition()) {
                action.jumpTo(targetPosition - maxDifference)
                return
            }
        }
        // Otherwise let superclass handle scrolling normally.
        super.onSeekTargetStep(dx, dy, state, action)
    }
}