package com.acnteam.crashybird.domain.model.game

data class PipePair(
    var x : Float,
    val y: Float,
    val topHeight: Float,
    val bottomHeight: Float,
    var scored : Boolean = false
)
