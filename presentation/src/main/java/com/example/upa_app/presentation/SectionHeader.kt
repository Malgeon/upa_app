package com.example.upa_app.presentation

import androidx.annotation.StringRes

data class SectionHeader(
    @StringRes val titleId: Int,
    val useHorizontalPadding: Boolean = true
)