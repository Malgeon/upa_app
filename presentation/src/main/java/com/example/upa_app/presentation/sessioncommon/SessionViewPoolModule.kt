package com.example.upa_app.presentation.sessioncommon

import androidx.recyclerview.widget.RecyclerView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Named

/**
 * Provides [RecyclerView.RecycledViewPool]s to share views between [RecyclerView]s.
 * E.g. Between difference days of the schedule.
 */
@InstallIn(FragmentComponent::class)
@Module
internal class SessionViewPoolModule {

    @FragmentScoped
    @Provides
    @Named("sessionViewPool")
    fun providesSessionViewPool(): RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    @FragmentScoped
    @Provides
    @Named("tagViewPool")
    fun providesTagViewPool(): RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
}