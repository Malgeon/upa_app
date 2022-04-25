package com.example.upa_app.di

import android.content.Context
import android.net.ConnectivityManager
import com.example.upa_app.data.db.AppDatabase
import com.example.upa_app.data.pref.PreferenceStorage
import com.example.upa_app.presentation.signin.SignInViewModelDelegate
import com.example.upa_app.shared.analytics.AnalyticsHelper
import com.example.upa_app.shared.di.ApplicationScope
import com.example.upa_app.shared.di.DefaultDispatcher
import com.example.upa_app.util.FirebaseAnalyticsHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {


    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    @ApplicationScope
    @Singleton
    @Provides
    fun providesApplicationScope(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)

//    @Singleton
//    @Provides
//    @MainThreadHandler
//    fun provideMainThreadHandler(): IOSchedHandler = IOSchedMainHandler()

    @Singleton
    @Provides
    fun provideAnalyticsHelper(
        @ApplicationScope applicationScope: CoroutineScope,
        signInDelegate: SignInViewModelDelegate,
        preferenceStorage: PreferenceStorage
    ): AnalyticsHelper =
        FirebaseAnalyticsHelper(applicationScope, signInDelegate, preferenceStorage)

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.buildDatabase(context)
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }
}