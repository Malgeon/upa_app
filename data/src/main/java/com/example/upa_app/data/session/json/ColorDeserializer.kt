package com.example.upa_app.data.session.json

import android.graphics.Color
import com.example.upa_app.shared.util.ColorUtils
import timber.log.Timber

fun parseColor(colorString: String?): Int {
    return if (colorString != null) {
        try {
            ColorUtils.parseHexColor(colorString)
        } catch (t: Throwable) {
            Timber.d(t, "Failed to parse color")
            Color.TRANSPARENT
        }
    } else {
        Color.TRANSPARENT
    }
}
