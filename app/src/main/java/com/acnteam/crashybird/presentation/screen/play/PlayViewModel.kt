package com.acnteam.crashybird.presentation.screen.play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.model.BackgroundMusic
import com.acnteam.crashybird.domain.model.BackgroundMusicSettings
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.SoundEffectSettings
import com.acnteam.crashybird.domain.model.game.CharacterPlayState
import com.acnteam.crashybird.domain.model.game.Firefly
import com.acnteam.crashybird.domain.model.game.Game
import com.acnteam.crashybird.domain.model.game.GameStatus
import com.acnteam.crashybird.domain.model.game.LevelPlayState
import com.acnteam.crashybird.domain.model.game.PipePair
import com.acnteam.crashybird.domain.usecase.AddCoinUseCase
import com.acnteam.crashybird.domain.usecase.GetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.GetBestScoreUseCase
import com.acnteam.crashybird.domain.usecase.GetSoundEffectSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SetSoundEffectSettingsUseCase
import com.acnteam.crashybird.domain.usecase.UpdateLevelUseCase
import com.acnteam.crashybird.domain.usecase.UpdatePlaySectionUseCase
import com.acnteam.crashybird.presentation.screen.play.component.SoundEffectPlayer
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
    private val updatePlaySectionUseCase: UpdatePlaySectionUseCase,
    private val getBestScoreUseCase: GetBestScoreUseCase,
) : ViewModel() {

    private var gameConfig = Game()

    //game job
    val currentGameConfig: Game get() = gameConfig

    private var loopJob: Job? = null
    private var windJob: Job? = null
    private var invincibilityJob: Job? = null

    var characterState by mutableStateOf<CharacterPlayState?>(null)
        private set
    var levelState by mutableStateOf<LevelPlayState?>(null)
        private set
    var gameStatus by mutableStateOf(GameStatus.Idle)
        private set
    var currentScore by mutableStateOf(0)
        private set
    var pipePairs by mutableStateOf(listOf<PipePair>())
        private set
    private var windDirectionToggle = true

    var fireflies = mutableStateListOf<Firefly>()
        private set
    var bestScore by mutableStateOf(0)
        private set

    var hasWatchedAd by mutableStateOf(false)
        private set
    var isInvincible by mutableStateOf(false)
        private set

    private var velocityY = 0f
    private var screenWidth = 0
    private var screenHeight = 0
    private val windEffectX = 0.5f
    private val windEffectY = 0.3f

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
        startWindLoop()
    }

    fun pauseGame() {
        if (gameStatus == GameStatus.Started) {
            gameStatus = GameStatus.Paused
            loopJob?.cancel()
            windJob?.cancel()
        }
    }

    fun resumeGame() {
        if (gameStatus == GameStatus.Paused) {
            gameStatus = GameStatus.Started
            startGameLoop()
            startWindLoop()
        }
    }

    private fun resetGame() {
        currentScore = 0
        velocityY = 0f
        pipePairs = emptyList()
        fireflies.clear()
        levelState = levelState?.copy(firefliesCollected = 0)

        hasWatchedAd = false
        isInvincible = false
        invincibilityJob?.cancel()

        val character = characterState?.character ?: return
        characterState = CharacterPlayState(
            character = character,
            x = (screenWidth / 4).toFloat(),
            y = (screenHeight / 2).toFloat(),
            radius = gameConfig.birdRadius
        )
        val level = levelState?.level ?: return
        loadBestScore(level)
    }

    fun revive() {
        val character = characterState?.character ?: return

        hasWatchedAd = true
        gameStatus = GameStatus.Reviving

        characterState = CharacterPlayState(
            character = character,
            x = (screenWidth / 4).toFloat(),
            y = (screenHeight / 2).toFloat(),
            radius = gameConfig.birdRadius
        )

        velocityY = 0f

        viewModelScope.launch {
            delay(3000L) // Hiệu ứng hồi sinh
            gameStatus = GameStatus.Started
            activateInvincibility()
            startGameLoop()
            startWindLoop()
        }
    }

    private fun activateInvincibility() {
        isInvincible = true
        invincibilityJob?.cancel()
        invincibilityJob = viewModelScope.launch {
            delay(3000L)
            isInvincible = false
        }
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

        velocityY = (velocityY + gameConfig.gravity + level.windY * windEffectY)

        val newX = character.x
        val newY = (character.y + velocityY).coerceIn(0f, screenHeight - character.radius)

        characterState = character.copy(x = newX, y = newY)

        if (newY >= screenHeight - character.radius) {
            gameOver()
            return
        }

        updatePipes()
        val levelWindX = levelState?.windX ?: 0f
        val fireflyVelocity = gameConfig.pipeVelocity + levelWindX * windEffectX

        fireflies.replaceAll { firefly ->
            firefly.copy(x = firefly.x - fireflyVelocity)
        }

        fireflies.removeAll { it.x < -it.radius }
        checkFireflyCollision(character)

        if (!isInvincible && checkCollisions()) {
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

    fun canWatchReviveAd(): Boolean {
        return !hasWatchedAd && gameStatus == GameStatus.Over
    }

    //pipe
    private fun updatePipes() {
        val levelWindX = levelState?.windX ?: 0f
        val pipeVelocity = gameConfig.pipeVelocity + levelWindX * windEffectX

        val updatedPipes = pipePairs.map { pipe ->
            var newBottomHeight = pipe.bottomHeight
            pipe.targetBottomHeight?.let { target ->
                if (newBottomHeight < target) {
                    val riseSpeed = 5f + pipeVelocity * 0.5f
                    newBottomHeight = (newBottomHeight + riseSpeed).coerceAtMost(target)
                }
            }

            var newTopHeight = pipe.topHeight
            pipe.targetTopHeight?.let { target ->
                if (newTopHeight < target) {
                    val dropSpeed = 5f + pipeVelocity * 0.5f
                    newTopHeight = (newTopHeight + dropSpeed).coerceAtMost(target)
                }
            }

            val gapY = screenHeight - newBottomHeight - gameConfig.pipeGapSize / 2

            pipe.copy(
                x = pipe.x - pipeVelocity,
                bottomHeight = newBottomHeight,
                topHeight = newTopHeight,
                y = gapY
            )
        }

        val filteredPipes = updatedPipes.filter {
            it.x + gameConfig.pipeWidth > 0
        }

        val finalPipes = if (filteredPipes.isEmpty() || filteredPipes.last().x < screenWidth / 2) {
            val newPipe = generateNewPipe()
            val previousPipe = filteredPipes.lastOrNull()
            if (previousPipe != null) {
                spawnFirefly(previousPipe, newPipe)
            }
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
        val finalTopHeight = Random.nextFloat() * (maxTopHeight - minTopHeight) + minTopHeight
        val finalBottomHeight = screenHeight - finalTopHeight - gameConfig.pipeGapSize
        return if (levelState?.level?.levelNumber == 5) {
            PipePair(
                x = initialPipeX,
                y = finalTopHeight + gameConfig.pipeGapSize / 2,
                topHeight = 0f,
                bottomHeight = 0f,
                scored = false,
                targetTopHeight = finalTopHeight,
                targetBottomHeight = finalBottomHeight
            )
        } else if (levelState?.level?.levelNumber in listOf(3, 4)) {
            PipePair(
                x = initialPipeX,
                y = finalTopHeight + gameConfig.pipeGapSize / 2,
                topHeight = finalTopHeight,
                bottomHeight = 0f,
                scored = false,
                targetBottomHeight = finalBottomHeight
            )
        } else {
            PipePair(
                x = initialPipeX,
                y = finalTopHeight + gameConfig.pipeGapSize / 2,
                topHeight = finalTopHeight,
                bottomHeight = finalBottomHeight,
                scored = false
            )
        }
    }

    private fun spawnFirefly(pipe: PipePair, nextPipe: PipePair) {
        val gapTop = pipe.y - gameConfig.pipeGapSize / 2
        val gapBottom = pipe.y + gameConfig.pipeGapSize / 2

        val randomY = Random.nextFloat() * (gapBottom - gapTop) + gapTop

        val minX = pipe.x + gameConfig.pipeWidth / 2
        val maxX = nextPipe.x - gameConfig.pipeWidth / 2

        val fireflyX = Random.nextFloat() * (maxX - minX) + minX

        val newFirefly = Firefly(
            x = fireflyX.toFloat(),
            y = randomY,
            radius = 20f
        )

        fireflies.add(newFirefly)
    }

    private fun checkFireflyCollision(character: CharacterPlayState) {
        val collected = mutableListOf<Firefly>()

        fireflies.forEach { firefly ->
            val dx = character.x - firefly.x
            val dy = character.y - firefly.y
            val distance = kotlin.math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            if (distance < character.radius + firefly.radius) {
                collected.add(firefly)
                levelState = levelState?.copy(
                    firefliesCollected = (levelState?.firefliesCollected ?: 0) + 1
                )
            }
        }

        fireflies.removeAll(collected)
    }

    //Score
    fun loadBestScore(level: Level) {
        viewModelScope.launch {
            bestScore = getBestScoreUseCase(level)
        }
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

        if ((levelState?.firefliesCollected ?: 0) >= (levelState?.firefliesRequired ?: 0)) {
            completeLevel()
        }
    }

    fun saveBestScore(level: Level, score: Int) {
        viewModelScope.launch {
            updatePlaySectionUseCase(level, score)
        }
    }

    private fun completeLevel() {
        gameStatus = GameStatus.Complete
        loopJob?.cancel()
        windJob?.cancel()
        invincibilityJob?.cancel()
        isInvincible = false
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
        windJob?.cancel()
        invincibilityJob?.cancel()
        isInvincible = false
        soundEffectPlayer.playGameOver()

        val currentLevel = levelState?.level ?: return
        if (currentScore > bestScore) {
            saveBestScore(currentLevel, currentScore)
            bestScore = currentScore
        }
        viewModelScope.launch {
            addCoinUseCase(currentScore)
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
        invincibilityJob?.cancel()
    }

    fun startWindLoop() {
        windJob?.cancel()
        windJob = viewModelScope.launch {
            while (isActive) {
                windDirectionToggle = !windDirectionToggle
                updateWind()
                delay(5_000L)
                levelState = levelState?.copy(windX = 0f, windY = 0f)
                delay(2_000L)
            }
        }
    }

    private fun updateWind() {
        val level = levelState?.level ?: return
        var (windX, windY) = getWindForLevel(level.levelNumber)

        if (level.levelNumber == 2 || level.levelNumber == 3) {
            windX = if (windDirectionToggle) windX else -windX
        }

        levelState = levelState?.copy(windX = windX, windY = windY)
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

    private val _soundEffectSettings = getSoundEffectSettingsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SoundEffectSettings(
            isEnabled = false,
            volume = 0.5f
        )
    )
    val soundEffectSettings: StateFlow<SoundEffectSettings> = _soundEffectSettings

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
            1 -> Pair(0f, 0f)
            2 -> Pair(2f, 0f)
            3 -> Pair(2f, 0f)
            4 -> Pair(2f, 2f)
            5 -> Pair(2f, 2f)
            else -> Pair(0f, 0f)
        }
    }
}