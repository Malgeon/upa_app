package com.example.upa_app.presentation.sessioncommon

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.upa_app.model.Tag
import com.example.upa_app.presentation.R

@BindingAdapter("topicTags")
fun topicTags(recyclerView: RecyclerView, topicTags: List<Tag>?) {
    if (topicTags?.isNotEmpty() == true) {
        recyclerView.isVisible = true
        recyclerView.adapter = (recyclerView.adapter as? TagAdapter ?: TagAdapter())
            .apply {
                tags = topicTags
            }
    } else {
        recyclerView.isGone = true
    }
}

@BindingAdapter("tagTint")
fun tagTint(textView: TextView, color: Int) {
    // Tint the colored dot
    (textView.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(
        tagTintOrDefault(
            color,
            textView.context
        )
    )
}

fun tagTintOrDefault(color: Int, context: Context): Int {
    return if (color != Color.TRANSPARENT) {
        color
    } else {
        ContextCompat.getColor(context, R.color.default_tag_color)
    }
}