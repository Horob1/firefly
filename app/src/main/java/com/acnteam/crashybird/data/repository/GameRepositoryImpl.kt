package com.acnteam.crashybird.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.repository.GameRepository
import com.acnteam.crashybird.presentation.widget.mapplayer.MapPlayerWidget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.gameDataStore by preferencesDataStore(name = GameRepositoryImpl.GAME_STORE)

class GameRepositoryImpl(private val context: Context) : GameRepository {
    companion object {
        const val GAME_STORE = "game_data"
    }

    private val dataStore = context.gameDataStore

    private object PreferencesKeys {
        val CURRENT_LEVEL = intPreferencesKey("current_level")
        val COIN = intPreferencesKey("coin")

        val CURRENT_CHARACTER = stringPreferencesKey("current_character")
    }

    override fun getCoin(): Flow<Int> {
        return dataStore.data.map {
            it[PreferencesKeys.COIN] ?: 0
        }
    }

    override fun getCurrentLevel(): Flow<Level> {
        return dataStore.data.map {
            val levelNumber = it[PreferencesKeys.CURRENT_LEVEL] ?: 1
            Level.getByLevelNumber(levelNumber)
        }
    }

    override fun getCurrentCharacter(): Flow<Character> {
        return dataStore.data.map {
            val characterName = it[PreferencesKeys.CURRENT_CHARACTER] ?: ""
            Character.getByName(characterName)
        }

    }

    override suspend fun setCurrentCharacter(character: Character) {
        dataStore.edit {
            it[PreferencesKeys.CURRENT_CHARACTER] = character.name
        }
    }

    override suspend fun setCurrentLevel(level: Level) {
        dataStore.edit {
            it[PreferencesKeys.CURRENT_LEVEL] = level.levelNumber
        }
        MapPlayerWidget.updateLevel(context, level.levelNumber)
    }

    override suspend fun setCoin(coin: Int) {
        dataStore.edit {
            it[PreferencesKeys.COIN] = coin
        }
    }

    override suspend fun addCoin(coin: Int) {
        dataStore.edit {
            it[PreferencesKeys.COIN] = (it[PreferencesKeys.COIN] ?: 0) + coin
        }
    }

    override suspend fun withdrawCoin(coin: Int) {
        dataStore.edit {
            it[PreferencesKeys.COIN] = (it[PreferencesKeys.COIN] ?: 0) - coin
        }
    }
}