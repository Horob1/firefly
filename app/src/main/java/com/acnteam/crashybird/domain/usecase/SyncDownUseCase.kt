package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.local.CharacterLocalDataSource
import com.acnteam.crashybird.domain.local.LevelLocalDataSource
import com.acnteam.crashybird.domain.repository.GameRepository
import com.acnteam.crashybird.domain.repository.UserRepository

class SyncDownUseCase(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    private val levelLocalDataSource: LevelLocalDataSource,
    private val characterLocalDataSource: CharacterLocalDataSource,
) {
    suspend operator fun invoke() {
        val data = userRepository.getUserDataFromServer()
        gameRepository.setCoin(data.first)
        levelLocalDataSource.setLevelList(data.second)
        characterLocalDataSource.setCharacterList(data.third)
    }
}