package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.RankState
import com.acnteam.crashybird.domain.repository.RankRepository

class FetchRankOfLevelUseCase(
    private val rankRepository: RankRepository,
) {
    suspend operator fun invoke(levelNumber: Int): List<RankState> {
        val result = rankRepository.fetchRankOfLevel(levelNumber)
        if (result.size < 5) {

            val newResult = mutableListOf<RankState>()
            newResult.addAll(result)
            for (
            i in 1..5 - result.size
            ) {
                newResult.add(
                    RankState(
                        level = Level.getByLevelNumber(levelNumber),
                        username = "NO USER",
                        score = 0
                    )
                )
            }
            return newResult
        } else return result
    }

}