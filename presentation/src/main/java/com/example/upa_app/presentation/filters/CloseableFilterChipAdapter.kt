package com.example.upa_app.presentation.filters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// TODO(jdkoren): Maybe combine this with SelectableFilterChipAdapter
/** Adapter for closeable filters, e.g. those above search results. */
class CloseableFilterChipAdapter(
    private val viewModelDelegate: FiltersViewModelDelegate
) : ListAdapter<FilterChip, FilterChipViewHolder>(FilterChipDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterChipViewHolder {
        return FilterChipViewHolder(
            ItemFilterChipCloseableBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ).apply {
                viewModel = viewModelDelegate
            }
        )
    }

    override fun onBindViewHolder(holder: FilterChipViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FilterChipViewHolder(
        private val binding: ItemFilterChipCloseableBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FilterChip) {
            binding.executeAfter {
                filterChip = item
            }
        }
    }
}

private object FilterChipDiff : DiffUtil.ItemCallback<FilterChip>() {
    override fun areItemsTheSame(oldItem: FilterChip, newItem: FilterChip) =
        oldItem.filter == newItem.filter

    override fun areContentsTheSame(oldItem: FilterChip, newItem: FilterChip) =
        oldItem.isSelected == newItem.isSelected
}