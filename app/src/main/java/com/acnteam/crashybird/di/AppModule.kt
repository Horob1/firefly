package com.acnteam.crashybird.di

import org.koin.core.module.Module

val appModules: List<Module> = listOf(
    viewModelModule,
    repositoryModule,
    useCaseModule,
    databaseModule,
    localModule,
    remoteModule
)