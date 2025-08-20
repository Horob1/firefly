package com.acnteam.crashybird.domain.model

data class BackgroundMusicSettings(
    val isEnabled: Boolean,
    val volume: Float,
    val music: BackgroundMusic,
)