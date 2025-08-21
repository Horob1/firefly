package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.BackgroundMusicSettings
import com.acnteam.crashybird.domain.repository.MusicRepository

class SetBackgroundMusicSettingsUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(backgroundMusicSettings: BackgroundMusicSettings) =
        musicRepository.setBackgroundMusicSettings(backgroundMusicSettings)
}