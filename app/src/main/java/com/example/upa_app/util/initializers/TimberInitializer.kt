package com.example.upa_app.util.initializers

import android.content.Context
import androidx.startup.Initializer
import timber.log.Timber

class TimberInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        Timber.plant(Timber.DebugTree())
//        if (BuildConfig.DEBUG) {
//            Timber.plant(Timber.DebugTree())
//        } else {
//            Timber.plant(CrashlyticsTree())
//        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
