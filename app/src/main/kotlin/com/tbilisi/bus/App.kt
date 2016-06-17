package com.tbilisi.bus

import android.app.Application
import android.os.StrictMode

class App : Application() {
    override fun onCreate() {
        // Enable strict mode in debug builds
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build())
        }

        DatabaseManager(this).initialize()

        super.onCreate()
    }
}