package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.RankState
import com.acnteam.crashybird.domain.repository.RankRepository


class FetchMyRankOfLevelUseCase(
    private val rankRepository: RankRepository,
) {
    suspend operator fun invoke(levelNumber: Int): RankState {
        return rankRepository.fetchMyRankOfLevel(levelNumber)
    }

}