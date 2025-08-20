package com.acnteam.crashybird.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LevelDto(
    val levelNumber: Int,
    val isUnlocked: Boolean,
    val highScore: Int = 0,
)