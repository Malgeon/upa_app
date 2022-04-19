package com.example.upa_app.presentation.di

import com.example.upa_app.domain.fcm.StagingTopicSubscriber
import com.example.upa_app.domain.fcm.TopicSubscriber
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module where classes created in the shared module are created.
 */
@InstallIn(SingletonComponent::class)
@Module
class SharedModule {

    @Singleton
    @Provides
    fun provideTopicSubscriber(): TopicSubscriber {
        return StagingTopicSubscriber()
    }
}