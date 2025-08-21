package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.local.LevelLocalDataSource
import com.acnteam.crashybird.domain.model.LevelState
import kotlinx.coroutines.flow.Flow

class GetLevelStateListUseCase(
    private val levelRepository: LevelLocalDataSource,
) {
    operator fun invoke(): Flow<List<LevelState>> = levelRepository.getLevelList()
}