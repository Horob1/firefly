package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentCharacterUseCase(
    private val gameRepository: GameRepository,
) {
    operator fun invoke(): Flow<Character> = gameRepository.getCurrentCharacter()
}