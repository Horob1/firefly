package com.acnteam.crashybird.domain.model.game

data class Firefly(
    var x: Float,
    var y: Float,
    val radius: Float = 40f,
    var collected: Boolean = false
)