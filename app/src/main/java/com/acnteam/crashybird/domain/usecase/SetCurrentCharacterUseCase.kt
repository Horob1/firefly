package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.repository.GameRepository

class SetCurrentCharacterUseCase(
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(character: Character) {
        gameRepository.setCurrentCharacter(character)
    }
}