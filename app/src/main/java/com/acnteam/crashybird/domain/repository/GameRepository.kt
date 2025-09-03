package com.acnteam.crashybird.domain.repository

import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.GraphicMode
import com.acnteam.crashybird.domain.model.Level
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getCoin(): Flow<Int>
    fun getCurrentLevel(): Flow<Level>

    fun getCurrentCharacter(): Flow<Character>

    suspend fun setCurrentCharacter(character: Character)

    suspend fun setCurrentLevel(level: Level)

    suspend fun setCoin(coin: Int)
    suspend fun addCoin(coin: Int)
    suspend fun withdrawCoin(coin: Int)

    fun getGraphicMode(): Flow<GraphicMode>
    suspend fun setGraphicMode(mode: GraphicMode)
}