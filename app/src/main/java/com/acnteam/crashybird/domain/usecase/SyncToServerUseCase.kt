package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.local.CharacterLocalDataSource
import com.acnteam.crashybird.domain.local.LevelLocalDataSource
import com.acnteam.crashybird.domain.repository.GameRepository
import com.acnteam.crashybird.domain.repository.UserRepository
import kotlinx.coroutines.flow.first

class SyncToServerUseCase(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    private val characterLocalDataSource: CharacterLocalDataSource,
    private val levelLocalDataSource: LevelLocalDataSource,
) {
    suspend operator fun invoke(): Result<Unit> {
        val coin = gameRepository.getCoin().first()
        val levels = levelLocalDataSource.getLevelList().first()
        val chars = characterLocalDataSource.getCharacterList().first()
        val result = userRepository.syncLocalToServer(coin, levels, chars)

        return if (result.isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

}