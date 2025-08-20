package com.acnteam.crashybird.domain.repository

import com.acnteam.crashybird.domain.model.Level
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getCoin(): Flow<Int>
    fun getCurrentLevel(): Flow<Level>
    suspend fun setCurrentLevel(level: Level)
    suspend fun addCoin(coin: Int)
    suspend fun withdrawCoin(coin: Int)
}