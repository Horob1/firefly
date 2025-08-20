package com.acnteam.crashybird.di

import com.acnteam.crashybird.presentation.screen.splash.SplashViewModel
import com.acnteam.crashybird.presentation.screen.welcome.WelcomeViewModel
import org.koin.dsl.module

val viewModelModule = module {
    single {
        SplashViewModel(get())
    }

    single {
        WelcomeViewModel(get())
    }
}