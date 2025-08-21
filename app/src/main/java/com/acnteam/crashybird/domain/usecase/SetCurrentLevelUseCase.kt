package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.repository.GameRepository

class SetCurrentLevelUseCase(
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(level: Level) = gameRepository.setCurrentLevel(level)
}