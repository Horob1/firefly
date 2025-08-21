package com.acnteam.crashybird.presentation.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.model.BackgroundMusic
import com.acnteam.crashybird.domain.model.BackgroundMusicSettings
import com.acnteam.crashybird.domain.model.SoundEffectSettings
import com.acnteam.crashybird.domain.usecase.GetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.GetSoundEffectSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SetSoundEffectSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    getBackgroundMusicSettingsUseCase: GetBackgroundMusicSettingsUseCase,
    getSoundEffectSettingsUseCase: GetSoundEffectSettingsUseCase,
    private val setBackgroundMusicSettingsUseCase: SetBackgroundMusicSettingsUseCase,
    private val setSoundEffectSettingsUseCase: SetSoundEffectSettingsUseCase,
) : ViewModel() {
    private val _backgroundMusicSettings = getBackgroundMusicSettingsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(
            5_000
        ),
        initialValue = BackgroundMusicSettings(
            music = BackgroundMusic.BacPhan,
            volume = 0.5f,
            isEnabled = false
        )
    )

    val backgroundMusicSettings: StateFlow<BackgroundMusicSettings> = _backgroundMusicSettings

    private val _soundEffectSettings = getSoundEffectSettingsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(
            5_000
        ),
        initialValue = SoundEffectSettings(
            isEnabled = false,
            volume = 0.5f
        )
    )

    val soundEffectSettings: StateFlow<SoundEffectSettings> = _soundEffectSettings

    fun setBackgroundMusicSettings(backgroundMusicSettings: BackgroundMusicSettings) {
        viewModelScope.launch {
            setBackgroundMusicSettingsUseCase(backgroundMusicSettings)
        }
    }

    fun setSoundEffectSettings(
        soundEffectSettings: SoundEffectSettings,
    ) {
        viewModelScope.launch {
            setSoundEffectSettingsUseCase(soundEffectSettings)
        }
    }
}