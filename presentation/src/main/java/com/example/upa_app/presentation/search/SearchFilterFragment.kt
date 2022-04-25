package com.example.upa_app.presentation.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.upa_app.presentation.R
import com.example.upa_app.presentation.filters.FiltersFragment
import com.example.upa_app.presentation.filters.FiltersViewModelDelegate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFilterFragment : FiltersFragment() {

    private val viewModel: SearchViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun resolveViewModelDelegate(): FiltersViewModelDelegate {
        return viewModel
    }
}