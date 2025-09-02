package com.acnteam.crashybird.domain.repository

import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.RankState

interface RankRepository {
    suspend fun getBestScore(level: Level): Int

    suspend fun upDateGameScore(score: Int, level: Level): Result<Unit>

    suspend fun fetchRank(): List<RankState>
}