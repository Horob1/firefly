package com.acnteam.crashybird.data.local

import com.acnteam.crashybird.data.local.dao.LevelDao
import com.acnteam.crashybird.domain.local.LevelLocalDataSource
import com.acnteam.crashybird.domain.model.LevelState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LevelLocalDataSourceImpl(
    private val levelDao: LevelDao,
) : LevelLocalDataSource {
    override fun getLevelList(): Flow<List<LevelState>> {
        return levelDao.getAllLevels().map {
            it.map { level ->
                level.toLevelState()
            }
        }
    }
}