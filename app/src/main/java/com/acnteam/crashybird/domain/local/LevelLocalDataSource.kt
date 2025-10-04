package com.acnteam.crashybird.domain.local

import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.LevelState
import kotlinx.coroutines.flow.Flow

interface LevelLocalDataSource {
    fun getLevelList(): Flow<List<LevelState>>

    suspend fun setLevelList(levels: List<LevelState>)

    suspend fun updateLevel(level: Level)

    suspend fun isNextLevelUnlocked(currentLevel: Int?): Boolean

}