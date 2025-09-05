package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.data.local.entity.ItemEntity
import com.acnteam.crashybird.domain.local.CharacterLocalDataSource
import com.acnteam.crashybird.domain.local.ItemLocalDataSource
import com.acnteam.crashybird.domain.local.LevelLocalDataSource
import com.acnteam.crashybird.domain.repository.GameRepository
import com.acnteam.crashybird.domain.repository.UserRepository

class SyncDownUseCase(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    private val levelLocalDataSource: LevelLocalDataSource,
    private val characterLocalDataSource: CharacterLocalDataSource,
    private val itemLocalDataSource: ItemLocalDataSource,
) {
    suspend operator fun invoke() {
        val data = userRepository.getUserDataFromServer()
        gameRepository.setCoin(data.first.first)
        levelLocalDataSource.setLevelList(data.first.second)
        characterLocalDataSource.setCharacterList(data.second.first)
        itemLocalDataSource.saveItems(data.second.second.map {
            ItemEntity(
                name = it.item.name,
                isUnlocked = it.isUnlocked
            )
        }
        )

    }
}