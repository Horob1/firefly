package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.GraphicMode
import com.acnteam.crashybird.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class GetGraphicModeUseCase(
    private val gameRepository: GameRepository,
) {
    operator fun invoke(): Flow<GraphicMode> = gameRepository.getGraphicMode()
}