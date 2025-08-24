package com.acnteam.crashybird.presentation.screen.play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.game.CharacterPlayState
import com.acnteam.crashybird.domain.model.game.Game
import com.acnteam.crashybird.domain.model.game.GameStatus
import com.acnteam.crashybird.domain.model.game.LevelPlayState
import com.acnteam.crashybird.domain.model.game.PipePair
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class PlayViewModel(
) : ViewModel() {

    private var gameConfig = Game()

    val currentGameConfig: Game get() = gameConfig

    var characterState by mutableStateOf<CharacterPlayState?>(null)
        private set
    var levelState by mutableStateOf<LevelPlayState?>(null)
        private set
    var gameStatus by mutableStateOf(GameStatus.Idle)
        private set
    var currentScore by mutableStateOf(0)
        private set
    var bestScore by mutableStateOf(0)
        private set
    var pipePairs by mutableStateOf(listOf<PipePair>())
        private set

    private var velocityY = 0f
    private var loopJob: Job? = null
    private var screenWidth = 0
    private var screenHeight = 0


    fun initializeGame(characterName: String, levelNumber: Int) {
        val character = Character.getByName(characterName)
        val level = Level.getByLevelNumber(levelNumber)

        characterState = CharacterPlayState(
            character = character,
            x = (screenWidth / 4).toFloat(),
            y = (screenHeight / 2).toFloat(),
            radius = gameConfig.birdRadius
        )

        levelState = LevelPlayState(
            level = level,
            windX = 0f,
            windY = 0f,
        )
    }

    fun updateScreenSize(width: Int, height: Int) {
        if (screenWidth != width || screenHeight != height) {
            screenWidth = width
            screenHeight = height
            gameConfig = gameConfig.copy(
                screenWidth = screenWidth,
                screenHeight = screenHeight
            )
            val character = characterState?.character ?: return
            characterState = CharacterPlayState(
                character = character,
                x = (screenWidth / 4).toFloat(),
                y = (screenHeight / 2).toFloat(),
                radius = gameConfig.birdRadius
            )
        }
    }

    fun start() {
        if (gameStatus == GameStatus.Started) return
        gameStatus = GameStatus.Started
        resetGame()
        startGameLoop()
    }

    fun restart() {
        gameStatus = GameStatus.Started
        resetGame()
        startGameLoop()
    }

    private fun resetGame() {
        currentScore = 0
        velocityY = 0f
        pipePairs = emptyList()

        val character = characterState?.character ?: return
        characterState = CharacterPlayState(
            character = character,
            x = (screenWidth / 4).toFloat(),
            y = (screenHeight / 2).toFloat(),
            radius = gameConfig.birdRadius
        )
    }

    private fun startGameLoop() {
        loopJob?.cancel()
        loopJob = viewModelScope.launch {
            while (isActive && gameStatus == GameStatus.Started) {
                updateFrame()
                delay(16L) // ~60 FPS
            }
        }
    }

    fun jump() {
        if (gameStatus != GameStatus.Started) return
        velocityY = gameConfig.birdJumpImpulse
    }

    private fun updateFrame() {
        val character = characterState ?: return

        velocityY = (velocityY + gameConfig.gravity)
            .coerceIn(-gameConfig.birdMaxVelocity, gameConfig.birdMaxVelocity)

        val newY = character.y + velocityY
        characterState = character.copy(y = newY)

        if (newY < 0 || newY > screenHeight) {
            gameOver()
            return
        }

        updatePipes()

        if (checkCollisions()) {
            gameOver()
            return
        }

        updateScore()
    }

    private fun updatePipes() {
        val updatedPipes = pipePairs.map { pipe ->
            pipe.copy(x = pipe.x - gameConfig.pipeVelocity)
        }

        val filteredPipes = updatedPipes.filter {
            it.x + gameConfig.pipeWidth > 0
        }

        val finalPipes = if (filteredPipes.isEmpty() ||
            filteredPipes.last().x < screenWidth / 2) {
            val newPipe = generateNewPipe()
            filteredPipes + newPipe
        } else {
            filteredPipes
        }

        pipePairs = finalPipes
    }

    private fun generateNewPipe(): PipePair {
        val initialPipeX = screenWidth.toFloat() + gameConfig.pipeWidth
        val minTopHeight = 100f
        val maxTopHeight = screenHeight - gameConfig.pipeGapSize - 100f
        val topHeight = Random.nextFloat() * (maxTopHeight - minTopHeight) + minTopHeight
        val bottomHeight = screenHeight - topHeight - gameConfig.pipeGapSize

        return PipePair(
            x = initialPipeX,
            y = topHeight + gameConfig.pipeGapSize / 2,
            topHeight = topHeight,
            bottomHeight = bottomHeight,
            scored = false
        )
    }

    private fun checkCollisions(): Boolean {
        val character = characterState ?: return false

        pipePairs.forEach { pipe ->
            if (isCollisionWithPipe(character, pipe)) {
                return true
            }
        }
        return false
    }

    private fun isCollisionWithPipe(character: CharacterPlayState, pipe: PipePair): Boolean {
        val birdRight = character.x + character.radius
        val birdLeft = character.x - character.radius
        val pipeLeft = pipe.x - gameConfig.pipeWidth / 2
        val pipeRight = pipe.x + gameConfig.pipeWidth / 2

        val horizontalCollision = birdRight > pipeLeft && birdLeft < pipeRight

        if (horizontalCollision) {
            val birdTop = character.y - character.radius
            val birdBottom = character.y + character.radius
            val gapTop = pipe.y - gameConfig.pipeGapSize / 2
            val gapBottom = pipe.y + gameConfig.pipeGapSize / 2

            val birdInGap = birdTop > gapTop && birdBottom < gapBottom
            return !birdInGap
        }

        return false
    }

    private fun updateScore() {
        val character = characterState ?: return

        val updatedPipes = pipePairs.map { pipe ->
            if (!pipe.scored && character.x > pipe.x + gameConfig.pipeWidth / 2) {
                currentScore += 1
                pipe.copy(scored = true)
            } else {
                pipe
            }
        }

        pipePairs = updatedPipes
    }

    private fun gameOver() {
        gameStatus = GameStatus.Over
        loopJob?.cancel()

        if (currentScore > bestScore) {
            bestScore = currentScore
        }
    }

    fun getBirdRotation(): Float {
        return when {
            velocityY > gameConfig.birdMaxVelocity / 1.1 -> 30f
            else -> 0f
        }
    }

    override fun onCleared() {
        super.onCleared()
        loopJob?.cancel()
    }
}