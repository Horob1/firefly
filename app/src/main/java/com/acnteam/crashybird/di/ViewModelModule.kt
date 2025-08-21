package com.acnteam.crashybird.di

import com.acnteam.crashybird.presentation.navigation.NavViewModel
import com.acnteam.crashybird.presentation.screen.home.HomeViewModel
import com.acnteam.crashybird.presentation.screen.setting.SettingViewModel
import com.acnteam.crashybird.presentation.screen.splash.SplashViewModel
import com.acnteam.crashybird.presentation.screen.welcome.WelcomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SplashViewModel(get())
    }

    viewModel {
        WelcomeViewModel(get())
    }

    viewModel {
        NavViewModel(get())
    }

    viewModel {
        SettingViewModel(get(), get(), get(), get())
    }

    viewModel {
        HomeViewModel(get(), get(), get(), get(), get())
    }

}