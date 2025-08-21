package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.local.CharacterLocalDataSource
import com.acnteam.crashybird.domain.model.Character

class UnlockCharacterUseCase(
    private val characterLocalDataSource: CharacterLocalDataSource,
) {
    suspend operator fun invoke(character: Character) {
        characterLocalDataSource.unlockCharacter(character)
    }
}