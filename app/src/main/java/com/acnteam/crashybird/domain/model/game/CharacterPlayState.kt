package com.acnteam.crashybird.domain.model.game

import com.acnteam.crashybird.domain.model.Character

data class CharacterPlayState(
    val character: Character,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)