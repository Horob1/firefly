package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.repository.RankRepository

class GetBestScoreUseCase(
    private val rankRepository: RankRepository,
) {
    suspend operator fun invoke(level: Level): Int {
        return rankRepository.getBestScore(level)
    }
}