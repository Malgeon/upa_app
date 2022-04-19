package com.example.upa_app.presentation.widget

import android.content.Context
import android.util.AttributeSet
import androidx.slidingpanelayout.widget.SlidingPaneLayout

// TODO(b/187348546) Remove when SlidingPaneLayout can support all MeasureSpec modes.
class IoSlidingPaneLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SlidingPaneLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpec = if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            // SlidingPaneLayout throws an exception when widthMode is not EXACTLY, so change to
            // EXACTLY and continue measuring.
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            MeasureSpec.makeMeasureSpec(
                if (widthSize > 0) widthSize else 500,
                MeasureSpec.EXACTLY
            )
        } else {
            widthMeasureSpec
        }

        val heightSpec = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            // SlidingPaneLayout throws an exception when heightMode is UNSPECIFIED, so change
            // to AT_MOST and continue measuring.
            val heightSize = MeasureSpec.getSize(widthMeasureSpec)
            MeasureSpec.makeMeasureSpec(
                if (heightSize > 0) heightSize else 500,
                MeasureSpec.AT_MOST
            )
        } else {
            heightMeasureSpec
        }
        super.onMeasure(widthSpec, heightSpec)
    }
}
