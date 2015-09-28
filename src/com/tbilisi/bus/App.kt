package com.tbilisi.bus

import android.app.Application
import com.squareup.leakcanary.LeakCanary

public class App : Application() {

    override fun onCreate() {
        super.onCreate()
        LeakCanary.install(this)
    }
}