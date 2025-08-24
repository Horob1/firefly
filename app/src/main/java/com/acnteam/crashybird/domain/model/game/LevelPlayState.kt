package com.acnteam.crashybird.domain.model.game

import com.acnteam.crashybird.domain.model.Level

data class LevelPlayState(
    val level: Level,
    val background: Int = level.image,
    val movingBackground: Int = level.movingImage,
    val pipeImage: Int = level.pipeImage,
    val pipeCapImage: Int = level.pipeCapImage,
    val windX: Float,
    val windY: Float,
)