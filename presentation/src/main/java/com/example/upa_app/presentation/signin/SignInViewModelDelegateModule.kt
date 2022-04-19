package com.example.upa_app.presentation.signin

import com.example.upa_app.domain.auth.ObserveUserAuthStateUseCase
import com.example.upa_app.shared.di.ApplicationScope
import com.example.upa_app.shared.di.IoDispatcher
import com.example.upa_app.shared.di.MainDispatcher
import com.example.upa_app.shared.di.ReservationEnabledFlag
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SignInViewModelDelegateModule {

    @Singleton
    @Provides
    fun provideSignInViewModelDelegate(
        dataSource: ObserveUserAuthStateUseCase,
//        notificationsPrefIsShownUseCase: NotificationsPrefIsShownUseCase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @MainDispatcher mainDispatcher: CoroutineDispatcher,
//        @ReservationEnabledFlag isReservationEnabledByRemoteConfig: Boolean,
        @ApplicationScope applicationScope: CoroutineScope
    ): SignInViewModelDelegate {
        return FirebaseSignInViewModelDelegate(
            observeUserAuthStateUseCase = dataSource,
//            notificationsPrefIsShownUseCase = notificationsPrefIsShownUseCase,
            ioDispatcher = ioDispatcher,
            mainDispatcher = mainDispatcher,
//            isReservationEnabledByRemoteConfig = isReservationEnabledByRemoteConfig,
            applicationScope = applicationScope
        )
    }


}