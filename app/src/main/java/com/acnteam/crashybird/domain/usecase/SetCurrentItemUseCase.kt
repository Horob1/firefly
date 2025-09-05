package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.Items
import com.acnteam.crashybird.domain.repository.GameRepository

class SetCurrentItemUseCase(
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(item: Items) = gameRepository.setCurrentItem(item)
}