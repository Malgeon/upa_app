package com.example.upa_app.model.filters

import com.example.upa_app.model.ConferenceDay
import com.example.upa_app.model.Tag

sealed class Filter {

    data class TagFilter(val tag: Tag) : Filter()

    data class DateFilter(val day: ConferenceDay) : Filter()

    object MyScheduleFilter : Filter()
}
