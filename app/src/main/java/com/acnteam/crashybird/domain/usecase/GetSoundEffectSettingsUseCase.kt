package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.SoundEffectSettings
import com.acnteam.crashybird.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class GetSoundEffectSettingsUseCase(
    private val musicRepository: MusicRepository,
) {
    operator fun invoke(): Flow<SoundEffectSettings> = musicRepository.getSoundEffectSettings()
}