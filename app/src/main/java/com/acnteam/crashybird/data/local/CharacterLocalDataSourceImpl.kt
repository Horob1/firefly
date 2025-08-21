package com.acnteam.crashybird.data.local

import com.acnteam.crashybird.data.local.dao.CharacterDao
import com.acnteam.crashybird.domain.local.CharacterLocalDataSource
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.CharacterState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharacterLocalDataSourceImpl(
    private val characterDao: CharacterDao,
) : CharacterLocalDataSource {
    override fun getCharacterList(): Flow<List<CharacterState>> {
        return characterDao.getAllCharacters().map {
            it.map { characterEntity ->
                characterEntity.toCharacterState()
            }
        }
    }

    override suspend fun unlockCharacter(character: Character) {
        characterDao.updateCharacter(character.name, true)
    }
}