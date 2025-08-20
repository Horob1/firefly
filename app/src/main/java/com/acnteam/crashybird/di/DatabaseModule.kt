package com.acnteam.crashybird.di

import androidx.room.Room
import com.acnteam.crashybird.data.local.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "game_db")
            .build()
    }

    single { get<AppDatabase>().levelDao }
    single { get<AppDatabase>().characterDao }

}