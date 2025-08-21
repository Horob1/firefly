package com.acnteam.crashybird.di

import com.acnteam.crashybird.data.repository.GameRepositoryImpl
import com.acnteam.crashybird.data.repository.MusicRepositoryImpl
import com.acnteam.crashybird.data.repository.OnBoardingRepositoryImpl
import com.acnteam.crashybird.domain.repository.GameRepository
import com.acnteam.crashybird.domain.repository.MusicRepository
import com.acnteam.crashybird.domain.repository.OnBoardingRepository
import org.koin.dsl.module


val repositoryModule = module {
    single<OnBoardingRepository> {
        OnBoardingRepositoryImpl(get())
    }

    single<MusicRepository> {
        MusicRepositoryImpl(get())
    }

    single<GameRepository> {
        GameRepositoryImpl(get())
    }
}