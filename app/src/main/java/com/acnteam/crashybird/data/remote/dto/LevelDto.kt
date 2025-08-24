package com.acnteam.crashybird.data.remote.dto

data class LevelDto(
    var levelNumber: Int = 2,
    var isUnlocked: Boolean = false,
    var highScore: Int = 0,
)