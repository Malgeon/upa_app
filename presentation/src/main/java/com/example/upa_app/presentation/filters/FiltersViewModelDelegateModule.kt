package com.example.upa_app.presentation.filters

import com.example.upa_app.shared.di.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope

@InstallIn(SingletonComponent::class)
@Module
class FiltersViewModelDelegateModule {

    @Provides
    fun provideFiltersViewModelDelegate(
        @ApplicationScope applicationScope: CoroutineScope
    ): FiltersViewModelDelegate = FiltersViewModelDelegateImpl(applicationScope)
}