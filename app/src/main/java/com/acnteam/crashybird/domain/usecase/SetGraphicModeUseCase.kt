package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.GraphicMode
import com.acnteam.crashybird.domain.repository.GameRepository

class SetGraphicModeUseCase(
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(mode: GraphicMode) {
        gameRepository.setGraphicMode(mode)
    }
}