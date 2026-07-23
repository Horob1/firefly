package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.local.LevelLocalDataSource
import com.acnteam.crashybird.domain.model.Level

class UpdateLevelUseCase(
    private val levelLocalDataSource: LevelLocalDataSource,
) {
    suspend operator fun invoke(level: Level) {
        // Mở khóa level hiện tại và tất cả các level trước đó để tránh lỗi bị khóa map
        for (i in 1..level.levelNumber) {
            levelLocalDataSource.updateLevel(Level.getByLevelNumber(i))
        }
    }
}
