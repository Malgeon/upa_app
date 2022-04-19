package com.example.upa_app.di

import android.content.Context
import com.example.upa_app.data.signin.datasources.AuthIdDataSource
import com.example.upa_app.data.signin.datasources.AuthStateUserDataSource
import com.example.upa_app.data.signin.datasources.RegisteredUserDataSource
import com.example.upa_app.domain.signin.StagingAuthStateUserDataSource
import com.example.upa_app.domain.signin.StagingRegisteredUserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
internal class SignInModule {

    @Singleton
    @Provides
    fun provideRegisteredUserDataSource(
        @ApplicationContext context: Context
    ): RegisteredUserDataSource {
        return StagingRegisteredUserDataSource(true)
    }

    @Singleton
    @Provides
    fun provideAuthStateUserDataSource(
        @ApplicationContext context: Context,
//        notificationAlarmUpdater: NotificationAlarmUpdater
    ): AuthStateUserDataSource {
        return StagingAuthStateUserDataSource(
            isRegistered = true,
            isSignedIn = true,
            context = context,
            userId = "StagingTest",
//            notificationAlarmUpdater = notificationAlarmUpdater
        )
    }

    @Singleton
    @Provides
    fun providesAuthIdDataSource(): AuthIdDataSource {
        return object : AuthIdDataSource {
            override fun getUserId() = "StagingTest"
        }
    }
}