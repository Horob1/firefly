package com.acnteam.crashybird.domain.usecase


import com.acnteam.crashybird.domain.repository.GameRepository

class AddCoinUseCase(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(coin: Int) = gameRepository.addCoin(coin)
}
