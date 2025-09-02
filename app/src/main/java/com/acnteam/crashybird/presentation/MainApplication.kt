package com.acnteam.crashybird.presentation

import android.app.Application
import com.acnteam.crashybird.di.appModules
import com.google.android.gms.ads.MobileAds
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        startKoin {
            androidContext(this@MainApplication)
            modules(appModules)
        }
    }
}