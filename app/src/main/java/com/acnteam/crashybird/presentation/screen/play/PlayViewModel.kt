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
import com.acnteam.crashybird.domain.model.GraphicMode
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
import com.acnteam.crashybird.domain.usecase.GetGraphicModeUseCase
import com.acnteam.crashybird.domain.usecase.GetSoundEffectSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SetSoundEffectSettingsUseCase
import com.acnteam.crashybird.domain.usecase.UpdateLevelUseCase
import com.acnteam.crashybird.domain.usecase.UpdatePlaySectionUseCase
import com.acnteam.crashybird.presentation.screen.play.component.SoundEffectPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class PlayViewModel(
    getBackgroundMusicSettingsUseCase: GetBackgroundMusicSettingsUseCase,
    getSoundEffectSettingsUseCase: GetSoundEffectSettingsUseCase,
    getGraphicModeUseCase: GetGraphicModeUseCase,
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
    private var slowEffectJob: Job? = null

    private val _graphicMode = getGraphicModeUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GraphicMode.LOW
    )

    val graphicMode: StateFlow<GraphicMode> = _graphicMode

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
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    //recovery time
    private var dashCooldownUntil = 0L
    private var highJumpCooldownUntil = 0L
    private var slowCooldownUntil = 0L
    private var isSlowEffectActive = false

    //cooldowns
    private val DASH_COOLDOWN = 20000L // 20 seconds
    private val HIGH_JUMP_COOLDOWN = 13000L // 13 seconds
    private val SLOW_COOLDOWN = 15000L // 15 seconds
    private val SLOW_EFFECT_DURATION = 3000L // 3 seconds

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
            width = character.frameWidth.toFloat(),
            height = character.frameHeight.toFloat(),
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
                width = character.frameWidth.toFloat(),
                height = character.frameHeight.toFloat(),
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
            slowEffectJob?.cancel()
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

        dashCooldownUntil = 0L
        highJumpCooldownUntil = 0L
        slowCooldownUntil = 0L
        isSlowEffectActive = false
        slowEffectJob?.cancel()

        val character = characterState?.character ?: return
        characterState = CharacterPlayState(
            character = character,
            x = (screenWidth / 4).toFloat(),
            y = (screenHeight / 2).toFloat(),
            width = character.frameWidth.toFloat(),
            height = character.frameHeight.toFloat(),
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
            width = character.frameWidth.toFloat(),
            height = character.frameHeight.toFloat(),
        )

        velocityY = 0f

        viewModelScope.launch {
            delay(3000L)
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

    //skill
    fun useDashSkill() {
        if (isDashOnCooldown() || gameStatus != GameStatus.Started) return

        dashCooldownUntil = System.currentTimeMillis() + DASH_COOLDOWN

        val character = characterState ?: return
        val sortedPipes = pipePairs.sortedBy { it.x }.filter { it.x > character.x }

        val dashDistance = if (sortedPipes.size >= 2) {
            val secondPipe = sortedPipes[1]
            secondPipe.x + gameConfig.pipeWidth + 50f - character.x
        } else if (sortedPipes.size == 1) {
            val firstPipe = sortedPipes[0]
            firstPipe.x + gameConfig.pipeWidth + 50f - character.x
        } else {
            400f
        }

        pipePairs = pipePairs.map { pipe ->
            pipe.copy(x = pipe.x - dashDistance)
        }

        fireflies.replaceAll { firefly ->
            firefly.copy(x = firefly.x - dashDistance)
        }

        soundEffectPlayer.playJump()
    }


    fun useHighJumpSkill() {
        if (isHighJumpOnCooldown() || gameStatus != GameStatus.Started) return
        highJumpCooldownUntil = System.currentTimeMillis() + HIGH_JUMP_COOLDOWN
        val level = levelState
        velocityY = if (level?.level?.levelNumber == 6)
            -(gameConfig.birdJumpImpulse * 10.0f) - (level.windY * windEffectY)
        else
            (gameConfig.birdJumpImpulse * 10.0f) - (level?.windY ?: 0f) * windEffectY
        soundEffectPlayer.playJump()
    }

    fun useSlowSkill() {
        if (isSlowOnCooldown() || gameStatus != GameStatus.Started) return
        slowCooldownUntil = System.currentTimeMillis() + SLOW_COOLDOWN
        isSlowEffectActive = true
        slowEffectJob?.cancel()
        slowEffectJob = viewModelScope.launch {
            delay(SLOW_EFFECT_DURATION)
            isSlowEffectActive = false
        }
        soundEffectPlayer.playJump()
    }

    fun isDashOnCooldown(): Boolean {
        return System.currentTimeMillis() < dashCooldownUntil
    }

    fun isHighJumpOnCooldown(): Boolean {
        return System.currentTimeMillis() < highJumpCooldownUntil
    }

    fun isSlowOnCooldown(): Boolean {
        return System.currentTimeMillis() < slowCooldownUntil
    }

    private fun startGameLoop() {
        loopJob?.cancel()
        loopJob = viewModelScope.launch {
            while (isActive && gameStatus == GameStatus.Started) {
                updateFrame()
                val gameFps = _graphicMode.value.fps
                val frameDelay =
                    if (isSlowEffectActive) (1000L / gameFps) * 2 else (1000L / gameFps)
                delay(frameDelay)
            }
        }
    }

    private fun updateFrame() {
        val character = characterState ?: return
        val level = levelState ?: return

        val gravityEffect =
            if (level.level.levelNumber == 6) -gameConfig.gravity else gameConfig.gravity
        val windEffect =
            if (level.level.levelNumber == 6) -level.windY * windEffectY else level.windY * windEffectY

        velocityY = velocityY + gravityEffect + windEffect

        val newX = character.x
        val halfHeight = character.height / 2f
        val newY = (character.y + velocityY).coerceIn(halfHeight, screenHeight - halfHeight)

        characterState = character.copy(x = newX, y = newY)

        if (level.level.levelNumber == 6) {
            if (newY <= halfHeight || newY >= screenHeight - halfHeight) {
                gameOver()
                return
            }
        } else {
            if (newY <= halfHeight || newY >= screenHeight - halfHeight) {
                gameOver()
                return
            }
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
        velocityY = if (level?.level?.levelNumber == 6)
            -gameConfig.birdJumpImpulse - level.windY * windEffectY
        else
            gameConfig.birdJumpImpulse - (level?.windY ?: 0f) * windEffectY
        soundEffectPlayer.playJump()
    }

    fun canWatchReviveAd(): Boolean {
        return !hasWatchedAd && gameStatus == GameStatus.Over
    }

    //pipe
    private fun updatePipes() {
        val levelWindX = levelState?.windX ?: 0f
        val baseVelocity = gameConfig.pipeVelocity

        val pipeVelocity = if (isSlowEffectActive) {
            (baseVelocity * 0.3f) + levelWindX * windEffectX * 0.3f
        } else {
            baseVelocity + levelWindX * windEffectX
        }

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
        return when (levelState?.level?.levelNumber) {
            in listOf(5, 6) -> {
                PipePair(
                    x = initialPipeX,
                    y = finalTopHeight + gameConfig.pipeGapSize / 2,
                    topHeight = 0f,
                    bottomHeight = 0f,
                    scored = false,
                    targetTopHeight = finalTopHeight,
                    targetBottomHeight = finalBottomHeight
                )
            }

            in listOf(3, 4) -> {
                PipePair(
                    x = initialPipeX,
                    y = finalTopHeight + gameConfig.pipeGapSize / 2,
                    topHeight = finalTopHeight,
                    bottomHeight = 0f,
                    scored = false,
                    targetBottomHeight = finalBottomHeight
                )
            }

            else -> {
                PipePair(
                    x = initialPipeX,
                    y = finalTopHeight + gameConfig.pipeGapSize / 2,
                    topHeight = finalTopHeight,
                    bottomHeight = finalBottomHeight,
                    scored = false
                )
            }
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
        val halfWidth = (character.width * 0.6) / 2f
        val halfHeight = (character.height * 0.75) / 2f


        fireflies.forEach { firefly ->
            val dx = character.x - firefly.x
            val dy = character.y - firefly.y
            val closestX = dx.coerceIn((-halfWidth).toFloat(), halfWidth.toFloat())
            val closestY = dy.coerceIn(-halfHeight.toFloat(), halfHeight.toFloat())

            val distX = dx - closestX
            val distY = dy - closestY
            val distanceSquared = distX * distX + distY * distY

            if (distanceSquared < firefly.radius * firefly.radius) {
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
        val halfWidth = (character.width * 0.6) / 2f
        val halfHeight = (character.height * 0.75) / 2f

        val birdLeft = character.x - halfWidth
        val birdRight = character.x + halfWidth
        val birdTop = character.y - halfHeight
        val birdBottom = character.y + halfHeight

        val pipeLeft = pipe.x - gameConfig.pipeWidth / 2
        val pipeRight = pipe.x + gameConfig.pipeWidth / 2

        val horizontalCollision = birdRight > pipeLeft && birdLeft < pipeRight

        if (horizontalCollision) {

            val currentTopPipeHeight = pipe.topHeight
            val currentBottomPipeHeight = pipe.bottomHeight

            if (currentTopPipeHeight > 0 && birdTop < currentTopPipeHeight) {
                return true
            }

            val bottomPipeTop = screenHeight - currentBottomPipeHeight
            if (currentBottomPipeHeight > 0 && birdBottom > bottomPipeTop) {
                return true
            }
            if (levelState?.level?.levelNumber == 6) {
                if (birdTop <= 0 || birdBottom >= screenHeight) {
                    return true
                }
            }
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
        val currentLevel = levelState?.level ?: return
        val nextLevelNumber = currentLevel.levelNumber + 1
        viewModelScope.launch {
            updateLevelUseCase(Level.getByLevelNumber(nextLevelNumber))
            _toastMessage.value = "Next map is open"
        }
    }

    private fun gameOver() {
        gameStatus = GameStatus.Over
        val character = characterState?.character ?: return
        loopJob?.cancel()
        windJob?.cancel()
        invincibilityJob?.cancel()
        slowEffectJob?.cancel()
        isInvincible = false
        isSlowEffectActive = false
        if (character.name == "Oii") {
            soundEffectPlayer.playOiiOver()
        } else {
            soundEffectPlayer.playGameOver()
        }
        val currentLevel = levelState?.level ?: return
        if (currentScore > bestScore) {
            saveBestScore(currentLevel, currentScore)
            bestScore = currentScore
        }
        viewModelScope.launch {
            addCoinUseCase(currentScore * 10)
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
        slowEffectJob?.cancel()
    }

    fun startWindLoop() {
        windJob?.cancel()
        windJob = viewModelScope.launch {
            while (isActive) {
                windDirectionToggle = !windDirectionToggle
                updateWind()
                val windDelay =
                    if (isSlowEffectActive) 10_000L else 5_000L // Slower wind changes during slow effect
                delay(windDelay)
                levelState = levelState?.copy(windX = 0f, windY = 0f)
                val calmDelay = if (isSlowEffectActive) 4_000L else 2_000L
                delay(calmDelay)
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
        return when (levelNumber) {
            1 -> Pair(0f, 0f)
            2 -> Pair(2f, 0f)
            3 -> Pair(2f, 0f)
            4 -> Pair(2f, 2f)
            5 -> Pair(2f, 2f)
            6 -> Pair(2f, 0f)
            else -> Pair(0f, 0f)
        }
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

}