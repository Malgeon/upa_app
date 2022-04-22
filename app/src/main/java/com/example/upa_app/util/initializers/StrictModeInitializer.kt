package com.example.upa_app.util.initializers

import android.content.Context
import android.os.StrictMode
import androidx.startup.Initializer
import com.jakewharton.threetenabp.AndroidThreeTen

class StrictModeInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}