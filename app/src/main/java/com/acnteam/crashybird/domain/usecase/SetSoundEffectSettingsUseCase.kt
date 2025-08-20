package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.SoundEffectSettings
import com.acnteam.crashybird.domain.repository.MusicRepository

class SetSoundEffectSettingsUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(soundEffectSettings: SoundEffectSettings) =
        musicRepository.setSoundEffectSettings(soundEffectSettings)
}