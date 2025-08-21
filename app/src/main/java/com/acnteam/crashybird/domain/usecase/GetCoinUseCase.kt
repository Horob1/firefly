package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class GetCoinUseCase(
    private val gameRepository: GameRepository,
) {
    operator fun invoke(): Flow<Int> = gameRepository.getCoin()
}