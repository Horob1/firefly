package com.acnteam.crashybird.di

import com.acnteam.crashybird.data.local.LevelLocalDataSourceImpl
import com.acnteam.crashybird.domain.local.LevelLocalDataSource
import org.koin.dsl.module

val localModule = module {
    single<LevelLocalDataSource> {
        LevelLocalDataSourceImpl(
            levelDao = get()
        )
    }
}