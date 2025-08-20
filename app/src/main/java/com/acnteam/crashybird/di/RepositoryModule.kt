package com.acnteam.crashybird.di

import com.acnteam.crashybird.data.repository.OnBoardingRepositoryImpl
import com.acnteam.crashybird.domain.repository.OnBoardingRepository
import org.koin.dsl.module


val repositoryModule = module {
    single<OnBoardingRepository> {
        OnBoardingRepositoryImpl(get())
    }
}