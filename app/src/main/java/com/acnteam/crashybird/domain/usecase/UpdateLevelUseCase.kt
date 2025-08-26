package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.local.LevelLocalDataSource
import com.acnteam.crashybird.domain.model.Level

class UpdateLevelUseCase (
    private val levelLocalDataSource: LevelLocalDataSource,
){
    suspend operator fun invoke(level: Level) {
        levelLocalDataSource.updateLevel(level)
    }
}