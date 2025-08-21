package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentLevelUseCase(
    private val gameRepository: GameRepository,
) {
    operator fun invoke(): Flow<Level> = gameRepository.getCurrentLevel()
}