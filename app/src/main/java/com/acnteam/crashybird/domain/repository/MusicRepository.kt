package com.acnteam.crashybird.domain.repository

import com.acnteam.crashybird.domain.model.BackgroundMusicSettings
import com.acnteam.crashybird.domain.model.SoundEffectSettings
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getBackgroundMusicSettings(): Flow<BackgroundMusicSettings>
    fun getSoundEffectSettings(): Flow<SoundEffectSettings>

    suspend fun setSoundEffectSettings(soundEffectSettings: SoundEffectSettings)
    suspend fun setBackgroundMusicSettings(backgroundMusicSettings: BackgroundMusicSettings)

}