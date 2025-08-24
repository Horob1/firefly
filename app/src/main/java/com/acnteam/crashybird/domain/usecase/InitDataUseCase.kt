package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.data.local.dao.CharacterDao
import com.acnteam.crashybird.data.local.dao.LevelDao
import com.acnteam.crashybird.data.local.entity.CharacterEntity
import com.acnteam.crashybird.data.local.entity.LevelEntity
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.Level
import kotlinx.coroutines.flow.first

class InitDataUseCase(
    private val levelDao: LevelDao,
    private val characterDao: CharacterDao,
) {
    suspend operator fun invoke() {
        if (levelDao.getAllLevels().first().isEmpty()) {
            levelDao.insertLevels(
                Level.getAll().map {
                    LevelEntity(
                        levelNumber = it.levelNumber,
                        isUnlocked = it.levelNumber == 1,
                        highScore = 0
                    )
                }
            )
        }
        if (characterDao.getAllCharacters().first().isEmpty()) {
            characterDao.insertCharacters(
                Character.getAll().map {
                    CharacterEntity(
                        name = it.name,
                        isUnlocked = false,
                    )
                }
            )
        }
    }
}