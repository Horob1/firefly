package com.acnteam.crashybird.di

import com.acnteam.crashybird.data.local.CharacterLocalDataSourceImpl
import com.acnteam.crashybird.data.local.ItemLocalDataSourceImpl
import com.acnteam.crashybird.data.local.LevelLocalDataSourceImpl
import com.acnteam.crashybird.domain.local.CharacterLocalDataSource
import com.acnteam.crashybird.domain.local.ItemLocalDataSource
import com.acnteam.crashybird.domain.local.LevelLocalDataSource
import org.koin.dsl.module

val localModule = module {
    single<LevelLocalDataSource> {
        LevelLocalDataSourceImpl(
            levelDao = get()
        )
    }

    single<CharacterLocalDataSource> {
        CharacterLocalDataSourceImpl(
            characterDao = get()
        )
    }

    single<ItemLocalDataSource> {
        ItemLocalDataSourceImpl(
            itemDao = get()
        )
    }
}