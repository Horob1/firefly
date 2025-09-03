package com.acnteam.crashybird.domain.model.game

data class Game(
    val screenWidth: Int = 0,
    val screenHeight: Int = 0,
    val gravity: Float = 0.8f, //trong luc
    val birdJumpImpulse: Float = -20f, //luc nhay
    val birdMaxVelocity: Float = 25f, //van toc max
    val pipeWidth: Float = 150f,
    val pipeVelocity: Float = 5f,
    val pipeGapSize: Float = 400f,
)
