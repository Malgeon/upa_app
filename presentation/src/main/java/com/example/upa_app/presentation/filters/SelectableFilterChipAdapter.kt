package com.example.upa_app.presentation.filters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.upa_app.presentation.R

/** Adapter for selectable filters, e.g. ones shown in the filter sheet. */
class SelectableFilterChipAdapter(
    private val viewModelDelegate: FiltersViewModelDelegate
) : ListAdapter<Any, RecyclerView.ViewHolder>(FilterChipAndHeadingDiff) {

    companion object {
        private val VIEW_TYPE_HEADING = R.layout.fading_snackbar_layout
        private val VIEW_TYPE_FILTER = R.layout.item_filter_chip_selectable

        /**
         * Inserts category headings in a list of [FilterChip]s to make a heterogeneous list.
         * Assumes the items are already grouped by [FilterChip.categoryLabel], beginning with
         * categoryLabel == '0'.
         */
        private fun insertCategoryHeadings(list: List<FilterChip>?): List<Any> {
            val newList = mutableListOf<Any>()
            var previousCategory = 0
            list?.forEach {
                val category = it.categoryLabel
                if (category != previousCategory && category != 0) {
                    newList += SectionHeader(
                        titleId = category,
                        useHorizontalPadding = false
                    )
                }
                newList.add(it)
                previousCategory = category
            }
            return newList
        }
    }

    override fun submitList(list: MutableList<Any>?) {
        exceptionInDebug(
            RuntimeException("call `submitEventFilterList()` instead to add category headings.")
        )
        super.submitList(list)
    }

    /** Prefer this method over [submitList] to add category headings. */
    fun submitFilterList(list: List<FilterChip>?) {
        super.submitList(insertCategoryHeadings(list))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SectionHeader -> VIEW_TYPE_HEADING
            is FilterChip -> VIEW_TYPE_FILTER
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADING -> createHeadingViewHolder(parent)
            VIEW_TYPE_FILTER -> createFilterViewHolder(parent)
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    private fun createHeadingViewHolder(parent: ViewGroup): HeadingViewHolder {
        return HeadingViewHolder(
            ItemGenericSectionHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private fun createFilterViewHolder(parent: ViewGroup): FilterViewHolder {
        val binding = ItemFilterChipSelectableBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).apply {
            viewModel = viewModelDelegate
        }
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeadingViewHolder -> holder.bind(getItem(position) as SectionHeader)
            is FilterViewHolder -> holder.bind(getItem(position) as FilterChip)
        }
    }

    /** ViewHolder for category heading items. */
    class HeadingViewHolder(
        private val binding: ItemGenericSectionHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        internal fun bind(item: SectionHeader) {
            binding.sectionHeader = item
            binding.executePendingBindings()
        }
    }

    /** ViewHolder for [FilterChip] items. */
    class FilterViewHolder(private val binding: ItemFilterChipSelectableBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.doOnAttach {
                binding.lifecycleOwner = itemView.findViewTreeLifecycleOwner()
            }
            itemView.doOnDetach {
                binding.lifecycleOwner = null
            }
        }

        internal fun bind(item: FilterChip) {
            binding.filterChip = item
            binding.executePendingBindings()
        }
    }
}

private object FilterChipAndHeadingDiff : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when (oldItem) {
            is FilterChip -> newItem is FilterChip && newItem.filter == oldItem.filter
            else -> oldItem == newItem // SectionHeader
        }
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when (oldItem) {
            is FilterChip -> oldItem.isSelected == (newItem as FilterChip).isSelected
            else -> true
        }
    }
}