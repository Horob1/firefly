package com.acnteam.crashybird.di

import com.acnteam.crashybird.presentation.navigation.NavViewModel
import com.acnteam.crashybird.presentation.screen.home.HomeViewModel
import com.acnteam.crashybird.presentation.screen.home.signin.SignInViewModel
import com.acnteam.crashybird.presentation.screen.play.PlayViewModel
import com.acnteam.crashybird.presentation.screen.rank.RankViewModel
import com.acnteam.crashybird.presentation.screen.rankdetail.RankDetailViewModel
import com.acnteam.crashybird.presentation.screen.setting.SettingViewModel
import com.acnteam.crashybird.presentation.screen.shop.ShopViewModel
import com.acnteam.crashybird.presentation.screen.splash.SplashViewModel
import com.acnteam.crashybird.presentation.screen.user.UserViewModel
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
        SettingViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    viewModel {
        HomeViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        PlayViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }


    viewModel {
        SignInViewModel()
    }

    viewModel {
        UserViewModel(get(), get())
    }

    viewModel {
        RankViewModel(get())
    }

    viewModel {
        RankDetailViewModel(get(), get())
    }

    viewModel {
        ShopViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}