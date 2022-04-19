package com.example.upa_app.shared.di

import com.example.upa_app.shared.config.AppConfigDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class FeatureFlagsModule {
//
//    @Provides
//    @Singleton
//    @ExploreArEnabledFlag
//    fun provideEnableExploreArFlag(appConfig: AppConfigDataSource): Boolean {
//        return appConfig.isExploreArFeatureEnabled()
//    }
//
//    @Provides
//    @MapFeatureEnabledFlag
//    fun provideMapFeatureEnabledFlag(appConfig: AppConfigDataSource): Boolean {
//        return appConfig.isMapFeatureEnabled()
//    }
//
//    @Provides
//    @CodelabsEnabledFlag
//    fun provideCodelabsEnabledFlag(appConfig: AppConfigDataSource): Boolean {
//        return appConfig.isCodelabsFeatureEnabled()
//    }
//
//    @Provides
//    @SearchScheduleEnabledFlag
//    fun provideSearchScheduleEnabledFlag(appConfig: AppConfigDataSource): Boolean {
//        return appConfig.isSearchScheduleFeatureEnabled()
//    }
//
//    @Provides
//    @SearchUsingRoomEnabledFlag
//    fun provideSearchUsingRoomEnabledFlag(appConfig: AppConfigDataSource): Boolean {
//        return appConfig.isSearchUsingRoomFeatureEnabled()
//    }
//
//    @Provides
//    @AssistantAppEnabledFlag
//    fun provideAssistantAppEnabledFlag(appConfig: AppConfigDataSource): Boolean {
//        return appConfig.isAssistantAppFeatureEnabled()
//    }
//
//    @Provides
//    @ReservationEnabledFlag
//    fun provideReservationEnabledFlag(appConfig: AppConfigDataSource): Boolean {
//        return appConfig.isReservationFeatureEnabled()
//    }
//
//    @Provides
//    @FeedEnabledFlag
//    fun provideFeedEnabledFlag(appConfig: AppConfigDataSource): Boolean {
//        return appConfig.isFeedEnabled()
//    }
}
