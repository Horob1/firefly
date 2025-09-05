package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.Items
import com.acnteam.crashybird.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentItemUseCase(
    private val gameRepository: GameRepository,
) {
    operator fun invoke(): Flow<Items> = gameRepository.getCurrentItem()
}