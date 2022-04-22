package com.example.upa_app.domain.search

import com.example.upa_app.data.ConferenceDataRepository
import com.example.upa_app.data.tag.TagRepository
import com.example.upa_app.domain.UseCase
import com.example.upa_app.model.Tag
import com.example.upa_app.model.filters.Filter
import com.example.upa_app.shared.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

private val FILTER_CATEGORIES = listOf(
    Tag.CATEGORY_TYPE,
    Tag.CATEGORY_TOPIC,
    Tag.CATEGORY_LEVEL
)

/** Loads filters for the Search screen. */
class LoadSearchFiltersUseCase @Inject constructor(
    private val conferenceRepository: ConferenceDataRepository,
    private val tagRepository: TagRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Unit, List<Filter>>(dispatcher) {

    override suspend fun execute(parameters: Unit): List<Filter> {
        val filters = mutableListOf<Filter>()
        filters.addAll(conferenceRepository.getConferenceDays().map { Filter.DateFilter(it) })
        filters.addAll(
            tagRepository.getTags()
                .filter { it.category in FILTER_CATEGORIES }
                .sortedWith(
                    compareBy({ FILTER_CATEGORIES.indexOf(it.category) }, { it.orderInCategory })
                )
                .map { Filter.TagFilter(it) }
        )
        return filters
    }
}