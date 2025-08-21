package com.acnteam.crashybird.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.model.BackgroundMusic
import com.acnteam.crashybird.domain.model.BackgroundMusicSettings
import com.acnteam.crashybird.domain.usecase.GetBackgroundMusicSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class NavViewModel(
    getBackgroundMusicSettingsUseCase: GetBackgroundMusicSettingsUseCase,
) : ViewModel() {
    private val _musicSettings = getBackgroundMusicSettingsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BackgroundMusicSettings(
            isEnabled = false,
            volume = 0.5f,
            music = BackgroundMusic.BacPhan
        )
    )

    val musicSettings: StateFlow<BackgroundMusicSettings> = _musicSettings
}