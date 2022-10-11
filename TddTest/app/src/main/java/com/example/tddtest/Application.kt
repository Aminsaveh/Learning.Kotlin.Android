package com.example.tddtest

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class TddTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidContext(this@TddTestApplication)
        }

        AppModule().init()
    }


    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }


}