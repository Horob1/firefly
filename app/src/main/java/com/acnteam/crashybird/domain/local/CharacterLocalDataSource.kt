package com.acnteam.crashybird.domain.local

import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.CharacterState
import kotlinx.coroutines.flow.Flow

interface CharacterLocalDataSource {
    fun getCharacterList(): Flow<List<CharacterState>>
    suspend fun setCharacterList(characters: List<CharacterState>)

    suspend fun unlockCharacter(character: Character)
}