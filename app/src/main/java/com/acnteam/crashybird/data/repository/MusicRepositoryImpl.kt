package com.acnteam.crashybird.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.acnteam.crashybird.domain.model.BackgroundMusic
import com.acnteam.crashybird.domain.model.BackgroundMusicSettings
import com.acnteam.crashybird.domain.model.SoundEffectSettings
import com.acnteam.crashybird.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.musicDataStore: DataStore<Preferences> by preferencesDataStore(name = MusicRepositoryImpl.MUSIC_STORE)

class MusicRepositoryImpl(context: Context) : MusicRepository {
    companion object {
        const val MUSIC_STORE = "MUSIC_STORE"

    }

    private val dataStore = context.musicDataStore


    private object PreferencesKey {
        val IS_ENABLED_BG_MUSIC = booleanPreferencesKey("is_enabled_bg_music")
        val VOLUME_BG_MUSIC = floatPreferencesKey("volume_bg_music")
        val MUSIC = stringPreferencesKey("music")

        val IS_ENABLED_SOUND_EFFECT = booleanPreferencesKey("is_enabled_sound_effect")
        val VOLUME_SOUND_EFFECT = floatPreferencesKey("volume_sound_effect")
    }

    override fun getBackgroundMusicSettings(): Flow<BackgroundMusicSettings> {
        return dataStore.data.map {
            BackgroundMusicSettings(
                isEnabled = it[PreferencesKey.IS_ENABLED_BG_MUSIC] ?: true,
                volume = it[PreferencesKey.VOLUME_BG_MUSIC] ?: 0.5f,
                music = BackgroundMusic.getByName(it[PreferencesKey.MUSIC] ?: "")
            )
        }
    }

    override fun getSoundEffectSettings(): Flow<SoundEffectSettings> {
        return dataStore.data.map {
            SoundEffectSettings(
                isEnabled = it[PreferencesKey.IS_ENABLED_SOUND_EFFECT] ?: true,
                volume = it[PreferencesKey.VOLUME_SOUND_EFFECT] ?: 0.5f,
            )
        }
    }

    override suspend fun setSoundEffectSettings(soundEffectSettings: SoundEffectSettings) {
        dataStore.edit {
            it[PreferencesKey.IS_ENABLED_SOUND_EFFECT] = soundEffectSettings.isEnabled
            it[PreferencesKey.VOLUME_SOUND_EFFECT] = soundEffectSettings.volume
        }
    }

    override suspend fun setBackgroundMusicSettings(backgroundMusicSettings: BackgroundMusicSettings) {
        dataStore.edit {
            it[PreferencesKey.IS_ENABLED_BG_MUSIC] = backgroundMusicSettings.isEnabled
            it[PreferencesKey.VOLUME_BG_MUSIC] = backgroundMusicSettings.volume
            it[PreferencesKey.MUSIC] = backgroundMusicSettings.music.name
        }
    }

}