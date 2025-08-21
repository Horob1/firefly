package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.local.CharacterLocalDataSource
import com.acnteam.crashybird.domain.model.CharacterState
import kotlinx.coroutines.flow.Flow

class GetCharacterListUseCase(
    private val characterLocalDataSource: CharacterLocalDataSource,
) {
    operator fun invoke(): Flow<List<CharacterState>> = characterLocalDataSource.getCharacterList()
}