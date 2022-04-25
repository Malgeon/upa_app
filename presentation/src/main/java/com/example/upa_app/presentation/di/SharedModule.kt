package com.example.upa_app.presentation.di

import com.example.upa_app.data.ConferenceDataRepository
import com.example.upa_app.data.ConferenceDataSource
import com.example.upa_app.data.db.AppDatabase
import com.example.upa_app.data.session.DefaultSessionRepository
import com.example.upa_app.data.session.SessionRepository
import com.example.upa_app.domain.fake.FakeAppConfigDataSource
import com.example.upa_app.domain.fake.FakeConferenceDataSource
import com.example.upa_app.domain.fake.userevent.FakeUserEventDataSource
import com.example.upa_app.domain.fcm.StagingTopicSubscriber
import com.example.upa_app.domain.fcm.TopicSubscriber
import com.example.upa_app.domain.search.FtsMatchStrategy
import com.example.upa_app.domain.search.SessionTextMatchStrategy
import com.example.upa_app.domain.search.SimpleMatchStrategy
import com.example.upa_app.domain.userevent.DefaultSessionAndUserEventRepository
import com.example.upa_app.domain.userevent.SessionAndUserEventRepository
import com.example.upa_app.domain.userevent.UserEventDataSource
import com.example.upa_app.shared.config.AppConfigDataSource
import com.example.upa_app.shared.di.SearchUsingRoomEnabledFlag
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/**
 * Module where classes created in the shared module are created.
 */
@InstallIn(SingletonComponent::class)
@Module
class SharedModule {

    // Define the data source implementations that should be used. All data sources are singletons.
    @Singleton
    @Provides
    @Named("remoteConfDatasource")
    fun provideConferenceDataSource(): ConferenceDataSource {
        return FakeConferenceDataSource
    }

    @Singleton
    @Provides
    @Named("bootstrapConfDataSource")
    fun provideBootstrapRemoteSessionDataSource(): ConferenceDataSource {
        return FakeConferenceDataSource
    }

    @Singleton
    @Provides
    fun provideConferenceDataRepository(
        @Named("remoteConfDatasource") remoteDataSource: ConferenceDataSource,
        @Named("bootstrapConfDataSource") boostrapDataSource: ConferenceDataSource,
        appDatabase: AppDatabase
    ): ConferenceDataRepository {
        return ConferenceDataRepository(remoteDataSource, boostrapDataSource, appDatabase)
    }

    @Singleton
    @Provides
    fun provideSessionRepository(
        conferenceDataRepository: ConferenceDataRepository
    ): SessionRepository {
        return DefaultSessionRepository(conferenceDataRepository)
    }

    @Singleton
    @Provides
    fun provideUserEventDataSource(): UserEventDataSource {
        return FakeUserEventDataSource
    }

//    @Singleton
//    @Provides
//    fun provideFeedbackEndpoint(): FeedbackEndpoint {
//        return FakeFeedbackEndpoint
//    }

    @Singleton
    @Provides
    fun provideSessionAndUserEventRepository(
        userEventDataSource: UserEventDataSource,
        sessionRepository: SessionRepository
    ): SessionAndUserEventRepository {
        return DefaultSessionAndUserEventRepository(
            userEventDataSource,
            sessionRepository
        )
    }

    @Singleton
    @Provides
    fun provideTopicSubscriber(): TopicSubscriber {
        return StagingTopicSubscriber()
    }

    @Singleton
    @Provides
    fun provideAppConfigDataSource(): AppConfigDataSource {
        return FakeAppConfigDataSource()
    }

//    @Singleton
//    @Provides
//    fun provideTimeProvider(): TimeProvider {
//        // TODO: Make the time configurable
//        return DefaultTimeProvider
//    }

//    @Singleton
//    @Provides
//    fun provideAnnouncementDataSource(): AnnouncementDataSource {
//        return FakeAnnouncementDataSource
//    }

//    @Singleton
//    @Provides
//    fun provideMomentDataSource(): MomentDataSource {
//        return FakeMomentDataSource
//    }

//    @Singleton
//    @Provides
//    fun provideFeedRepository(
//        announcementDataSource: AnnouncementDataSource,
//        momentDataSource: MomentDataSource
//    ): FeedRepository {
//        return DefaultFeedRepository(announcementDataSource, momentDataSource)
//    }

//    @Singleton
//    @Provides
//    fun provideArDebugFlagEndpoint(): ArDebugFlagEndpoint {
//        return FakeArDebugFlagEndpoint
//    }

    @Singleton
    @Provides
    fun provideSessionTextMatchStrategy(
        @SearchUsingRoomEnabledFlag useRoom: Boolean,
        appDatabase: AppDatabase
    ): SessionTextMatchStrategy {
        return if (useRoom) FtsMatchStrategy(appDatabase) else SimpleMatchStrategy
    }
}