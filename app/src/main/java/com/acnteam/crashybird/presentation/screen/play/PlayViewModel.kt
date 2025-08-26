package com.acnteam.crashybird.presentation.screen.play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.acnteam.crashybird.domain.local.LevelLocalDataSource
import com.acnteam.crashybird.domain.model.BackgroundMusic
import com.acnteam.crashybird.domain.model.BackgroundMusicSettings
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.SoundEffectSettings
import com.acnteam.crashybird.domain.model.game.CharacterPlayState
import com.acnteam.crashybird.domain.model.game.Game
import com.acnteam.crashybird.domain.model.game.GameStatus
import com.acnteam.crashybird.domain.model.game.LevelPlayState
import com.acnteam.crashybird.domain.model.game.PipePair
import com.acnteam.crashybird.domain.usecase.AddCoinUseCase
import com.acnteam.crashybird.domain.usecase.GetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.GetSoundEffectSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SetSoundEffectSettingsUseCase
import com.acnteam.crashybird.domain.usecase.UpdateLevelUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class PlayViewModel(
    getBackgroundMusicSettingsUseCase: GetBackgroundMusicSettingsUseCase,
    getSoundEffectSettingsUseCase: GetSoundEffectSettingsUseCase,
    private val setBackgroundMusicSettingsUseCase: SetBackgroundMusicSettingsUseCase,
    private val setSoundEffectSettingsUseCase: SetSoundEffectSettingsUseCase,
    private val soundEffectPlayer: SoundEffectPlayer,
    private val addCoinUseCase: AddCoinUseCase,
    private val updateLevelUseCase: UpdateLevelUseCase,
) : ViewModel() {

    private var gameConfig = Game()

    val currentGameConfig: Game get() = gameConfig
    private var windJob: Job? = null
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
    private val windEffectX = 0.5f   // hệ số ảnh hưởng gió ngang
    private val windEffectY = 0.3f   // hệ số ảnh hưởng gió dọc

    fun initializeGame(characterName: String, levelNumber: Int) {
        val character = Character.getByName(characterName)
        val level = Level.getByLevelNumber(levelNumber)

        characterState = CharacterPlayState(
            character = character,
            x = (screenWidth / 4).toFloat(),
            y = (screenHeight / 2).toFloat(),
            radius = gameConfig.birdRadius
        )

        val (windX, windY) = getWindForLevel(level.levelNumber)
        levelState = LevelPlayState(
            level = level,
            windX = windX,
            windY = windY
        )

    }

    fun startWindLoop() {
        windJob?.cancel()
        windJob = viewModelScope.launch {
            while (isActive) {
                // Chờ 7 giây trước khi gió xuất hiện
                delay(20_000L)

                // Áp dụng gió
                updateWind()

                // Gió tồn tại 3 giây
                delay(3_000L)

                // Tắt gió
                levelState = levelState?.copy(windX = 0f, windY = 0f)
            }
        }
    }


    private fun updateWind() {
        val level = levelState?.level ?: return
        val (windX, windY) = getWindForLevel(level.levelNumber)

        levelState = levelState?.copy(windX = windX, windY = windY)
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
        startWindLoop()
    }

    fun restart() {
        gameStatus = GameStatus.Started
        resetGame()
        startGameLoop()
    }
    fun pauseGame() {
        if (gameStatus == GameStatus.Started) {
            gameStatus = GameStatus.Paused
            loopJob?.cancel()
        }
    }
    fun resumeGame() {
        if (gameStatus == GameStatus.Paused) {
            gameStatus = GameStatus.Started
            startGameLoop()
        }
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

    private fun updateFrame() {
        val character = characterState ?: return
        val level = levelState ?: return

        // Tính vận tốc rơi = trọng lực + gió dọc
        velocityY = (velocityY + gameConfig.gravity - level.windY * windEffectY)
            .coerceIn(-gameConfig.birdMaxVelocity, gameConfig.birdMaxVelocity)

        val newX = character.x

        val newY = (character.y + velocityY).coerceIn(0f, screenHeight - character.radius)

        characterState = character.copy(x = newX, y = newY)
        

        if (newY >= screenHeight - character.radius) {
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

    fun jump() {
        if (gameStatus != GameStatus.Started) return
        val level = levelState
        velocityY = (gameConfig.birdJumpImpulse - (level?.windY ?: 0f) * windEffectY)
        soundEffectPlayer.playJump()
    }



    private fun updatePipes() {
        val levelWindX = levelState?.windX ?: 0f
        val adjustedPipeVelocity = gameConfig.pipeVelocity - levelWindX * windEffectX

        val updatedPipes = pipePairs.map { pipe ->
            pipe.copy(x = pipe.x - adjustedPipeVelocity)
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

        if (currentScore >= 10) {
            completeLevel()
        }
    }

    private fun completeLevel() {
        gameStatus = GameStatus.Over
        loopJob?.cancel()
        windJob?.cancel()
        soundEffectPlayer.playGameOver()

        val currentLevel = levelState?.level ?: return
        val nextLevelNumber = currentLevel.levelNumber + 1

        viewModelScope.launch {
            updateLevelUseCase(Level.getByLevelNumber(nextLevelNumber))
        }
    }

    private fun gameOver() {
        gameStatus = GameStatus.Over
        loopJob?.cancel()
        soundEffectPlayer.playGameOver()

        if (currentScore > bestScore) {
            bestScore = currentScore
        }
        viewModelScope.launch {
            addCoinUseCase(currentScore*1000)
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

    //Sound and Music
    private val _backgroundMusicSettings = getBackgroundMusicSettingsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BackgroundMusicSettings(
            music = BackgroundMusic.BacPhan,
            volume = 0.5f,
            isEnabled = false
        )
    )
    val backgroundMusicSettings: StateFlow<BackgroundMusicSettings> = _backgroundMusicSettings

    // --- Sound Effect ---
    private val _soundEffectSettings = getSoundEffectSettingsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SoundEffectSettings(
            isEnabled = false,
            volume = 0.5f
        )
    )
    val soundEffectSettings: StateFlow<SoundEffectSettings> = _soundEffectSettings

    // --- Update functions ---
    fun setBackgroundMusicSettings(backgroundMusicSettings: BackgroundMusicSettings) {
        viewModelScope.launch {
            setBackgroundMusicSettingsUseCase(backgroundMusicSettings)
        }
    }

    fun setSoundEffectSettings(soundEffectSettings: SoundEffectSettings) {
        viewModelScope.launch {
            setSoundEffectSettingsUseCase(soundEffectSettings)
        }
    }

    init {
        viewModelScope.launch {
            soundEffectSettings.collect { settings ->
                soundEffectPlayer.sfxEnabled = settings.isEnabled
                soundEffectPlayer.sfxVolume = settings.volume
            }
        }
    }
    fun getWindForLevel(levelNumber: Int): Pair<Float, Float> {
        return when(levelNumber) {
            1 -> Pair(2f, 0f)      // Map1: không gió
            2 -> Pair(-2f, 0f)     // Map2: ngược chiều (từ phải sang trái)
            3 -> Pair(2f, 0f)      // Map3: xuôi chiều (từ trái sang phải)
            4 -> Pair(-2f, 2f)     // Map4: ngược + trên xuống
            5 -> Pair(2f, 2f)      // Map5: xuôi chiều + trên xuống
            else -> Pair(0f, 0f)
        }
    }
}