package com.acnteam.crashybird.di

import com.acnteam.crashybird.domain.usecase.CompleteOnBoardingUseCase
import com.acnteam.crashybird.domain.usecase.GetOnBoardingStateUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // OnBoarding
    single {
        GetOnBoardingStateUseCase(get())
    }

    single {
        CompleteOnBoardingUseCase(get())
    }
}