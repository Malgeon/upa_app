package com.example.upa_app.presentation.filters

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

@BindingAdapter("activeFilters", "viewModel", requireAll = true)
fun activeFilters(
    recyclerView: RecyclerView,
    filters: List<FilterChip>?,
    viewModel: FiltersViewModelDelegate
) {
    val filterChipAdapter: CloseableFilterChipAdapter
    if (recyclerView.adapter == null) {
        filterChipAdapter = CloseableFilterChipAdapter(viewModel)
        recyclerView.apply {
            adapter = filterChipAdapter
            val space = resources.getDimensionPixelSize(R.dimen.spacing_micro)
            addItemDecoration(SpaceDecoration(start = space, end = space))
        }
    } else {
        filterChipAdapter = recyclerView.adapter as CloseableFilterChipAdapter
    }
    filterChipAdapter.submitList(filters ?: emptyList())
}

@BindingAdapter("showResultCount", "resultCount", requireAll = true)
fun filterHeader(textView: TextView, showResultCount: Boolean?, resultCount: Int?) {
    if (showResultCount == true && resultCount != null) {
        textView.text = textView.resources.getString(R.string.result_count, resultCount)
    } else {
        textView.setText(R.string.filters)
    }
}

@BindingAdapter("filterChipOnClick", "viewModel", requireAll = true)
fun filterChipOnClick(
    chip: Chip,
    filterChip: FilterChip,
    viewModel: FiltersViewModelDelegate
) {
    chip.setOnClickListener {
        viewModel.toggleFilter(filterChip.filter, chip.isChecked)
    }
}

@BindingAdapter("filterChipOnClose", "viewModel", requireAll = true)
fun filterChipOnClose(
    chip: Chip,
    filterChip: FilterChip,
    viewModel: FiltersViewModelDelegate
) {
    chip.setOnCloseIconClickListener {
        viewModel.toggleFilter(filterChip.filter, false)
    }
}

@BindingAdapter("filterChipText")
fun filterChipText(chip: Chip, filter: FilterChip) {
    if (filter.textResId != 0) {
        chip.setText(filter.textResId)
    } else {
        chip.text = filter.text
    }
}

@BindingAdapter("filterChipTint")
fun filterChipTint(chip: Chip, color: Int) {
    val tintColor = if (color != Color.TRANSPARENT) {
        color
    } else {
        ContextCompat.getColor(chip.context, R.color.default_tag_color)
    }
    chip.chipIconTint = ColorStateList.valueOf(tintColor)
}