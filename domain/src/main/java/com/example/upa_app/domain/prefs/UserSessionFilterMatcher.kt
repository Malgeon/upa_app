package com.example.upa_app.domain.prefs

import com.example.upa_app.model.filters.Filter
import com.example.upa_app.model.filters.Filter.MyScheduleFilter
import com.example.upa_app.model.filters.Filter.TagFilter
import com.example.upa_app.model.userdata.UserSession

class UserSessionFilterMatcher(filters: List<Filter>) {
    private val mySchedule = MyScheduleFilter in filters
    private val days = filters.filterIsInstance<Filter.DateFilter>().map { it.day }
    private val tagsByCategory =
        filters.filterIsInstance<TagFilter>().map { it.tag }.groupBy { it.category }

    internal fun matches(userSession: UserSession): Boolean {
        if (mySchedule && !userSession.userEvent.isStarredOrReserved()) {
            return false
        }
        if (days.isNotEmpty() && days.none { day -> userSession.session in day}) {
            return false
        }
        // For each category, session must have a tag that matches.
        val sessionTags = userSession.session.tags
        tagsByCategory.forEach { (_, tagsInCategory) ->
            if (sessionTags.intersect(tagsInCategory).isEmpty()) {
                return false
            }
        }
        return true
    }
}