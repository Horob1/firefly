package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.repository.RankRepository

class GetRanksUseCase(
    private val rankRepository: RankRepository,
) {
    suspend operator fun invoke() = rankRepository.fetchRank()
}