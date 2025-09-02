package com.acnteam.crashybird.domain.model.game

data class Firefly(
    var x: Float,
    var y: Float,
    val radius: Float = 20f,
    var collected: Boolean = false
)