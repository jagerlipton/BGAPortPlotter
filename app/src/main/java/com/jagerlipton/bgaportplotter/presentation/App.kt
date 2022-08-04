package com.jagerlipton.bgaportplotter.presentation

import android.app.Application
import com.jagerlipton.bgaportplotter.presentation.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(listOf(appModule))
            androidLogger(org.koin.core.logger.Level.NONE)
            androidContext(this@App)
        }
    }
}