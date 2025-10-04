package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.local.LevelLocalDataSource

class CheckNextLevelUnlockedUseCase(
    private val levelLocalDataSource: LevelLocalDataSource,
) {
    suspend operator fun invoke(level: Int?): Boolean {
        return levelLocalDataSource.isNextLevelUnlocked(level)
    }
}
