package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.model.BackgroundMusicSettings
import com.acnteam.crashybird.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class GetBackgroundMusicSettingsUseCase(
    private val musicRepository: MusicRepository,
) {
    operator fun invoke(): Flow<BackgroundMusicSettings> =
        musicRepository.getBackgroundMusicSettings()
}