package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.data.local.dao.CharacterDao
import com.acnteam.crashybird.data.local.dao.ItemDao
import com.acnteam.crashybird.data.local.dao.LevelDao
import com.acnteam.crashybird.data.local.entity.CharacterEntity
import com.acnteam.crashybird.data.local.entity.ItemEntity
import com.acnteam.crashybird.data.local.entity.LevelEntity
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.Items
import com.acnteam.crashybird.domain.model.Level
import kotlinx.coroutines.flow.first

class InitDataUseCase(
    private val levelDao: LevelDao,
    private val characterDao: CharacterDao,
    private val itemDao: ItemDao,
) {
    suspend operator fun invoke() {
        val allLevels = Level.getAll()
        val currentLevels = levelDao.getAllLevels().first()

        // 1. Luôn đảm bảo màn 1 được mở khóa
        levelDao.unlockLevel(1, isUnlocked = true)

        if (currentLevels.size < allLevels.size) {
            val missingLevels = allLevels.filter { level ->
                currentLevels.none { it.levelNumber == level.levelNumber }
            }
            levelDao.insertLevels(
                missingLevels.map {
                    LevelEntity(
                        levelNumber = it.levelNumber,
                        isUnlocked = it.levelNumber == 1,
                        highScore = 0
                    )
                }
            )
        }

        val allCharacters = Character.getAll()
        val currentCharacters = characterDao.getAllCharacters().first()

        // 2. Luôn đảm bảo nhân vật mặc định (Jack) được mở khóa
        characterDao.updateCharacter(Character.Jack.name, true)

        if (currentCharacters.size < allCharacters.size) {
            val missingCharacters = allCharacters.filter { char ->
                currentCharacters.none { it.name == char.name }
            }
            characterDao.insertCharacters(
                missingCharacters.map {
                    CharacterEntity(
                        name = it.name,
                        isUnlocked = it.name == Character.Jack.name,
                    )
                }
            )
        }

        val allItems = Items.getAll()
        val currentItems = itemDao.getAllItems().first()
        if (currentItems.size < allItems.size) {
            val missingItems = allItems.filter { item ->
                currentItems.none { it.name == item.name }
            }
            itemDao.insertItems(
                missingItems.map {
                    ItemEntity(
                        name = it.name,
                        isUnlocked = it.name == Items.ChildFirefly.name,
                    )
                }
            )
        }
    }
}
