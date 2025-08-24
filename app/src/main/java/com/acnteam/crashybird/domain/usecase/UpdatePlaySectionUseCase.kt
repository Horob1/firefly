package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.repository.RankRepository

class UpdatePlaySectionUseCase(
    private val rankRepository: RankRepository,
) {
    suspend operator fun invoke(level: Level, score: Int) =
        rankRepository.upDateGameScore(score, level)
}