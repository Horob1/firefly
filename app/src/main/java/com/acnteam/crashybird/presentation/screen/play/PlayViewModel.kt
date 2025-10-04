package com.acnteam.crashybird.presentation.screen.play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.model.BackgroundMusic
import com.acnteam.crashybird.domain.model.BackgroundMusicSettings
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.Enemy
import com.acnteam.crashybird.domain.model.GraphicMode
import com.acnteam.crashybird.domain.model.Items
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.SoundEffectSettings
import com.acnteam.crashybird.domain.model.game.BoostItem
import com.acnteam.crashybird.domain.model.game.BoostItemState
import com.acnteam.crashybird.domain.model.game.CharacterPlayState
import com.acnteam.crashybird.domain.model.game.Game
import com.acnteam.crashybird.domain.model.game.GameStatus
import com.acnteam.crashybird.domain.model.game.ItemPlayState
import com.acnteam.crashybird.domain.model.game.LevelPlayState
import com.acnteam.crashybird.domain.model.game.PipePair
import com.acnteam.crashybird.domain.model.game.big_item
import com.acnteam.crashybird.domain.model.game.immortal
import com.acnteam.crashybird.domain.model.game.mini_item
import com.acnteam.crashybird.domain.usecase.AddCoinUseCase
import com.acnteam.crashybird.domain.usecase.CheckNextLevelUnlockedUseCase
import com.acnteam.crashybird.domain.usecase.GetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.GetBestScoreUseCase
import com.acnteam.crashybird.domain.usecase.GetCurrentItemUseCase
import com.acnteam.crashybird.domain.usecase.GetGraphicModeUseCase
import com.acnteam.crashybird.domain.usecase.GetSoundEffectSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SetCurrentLevelUseCase
import com.acnteam.crashybird.domain.usecase.SetSoundEffectSettingsUseCase
import com.acnteam.crashybird.domain.usecase.UpdateLevelUseCase
import com.acnteam.crashybird.domain.usecase.UpdatePlaySectionUseCase
import com.acnteam.crashybird.presentation.screen.play.effect.SoundEffectPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
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
    getCurrentItemUseCase: GetCurrentItemUseCase,
    private val checkNextLevelUnlockedUseCase: CheckNextLevelUnlockedUseCase,
    private val setCurrentLevelUseCase: SetCurrentLevelUseCase
) : ViewModel() {

    private var loopJob: Job? = null


    private var gameConfig = Game()
    val currentGameConfig: Game get() = gameConfig


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
    var bestScore by mutableStateOf(0)
        private set
    var hasWatchedAd by mutableStateOf(false)
        private set
    var isInvincible by mutableStateOf(false)
        private set
    private var isBig by mutableStateOf(false)

    // Cooldowns
    private var dashCooldownUntil = 0L
    private var highJumpCooldownUntil = 0L
    private var slowCooldownUntil = 0L
    private var isSlowEffectActive = false

    private val DASH_COOLDOWN = 20000L
    private val HIGH_JUMP_COOLDOWN = 13000L
    private val SLOW_COOLDOWN = 15000L
    private val SLOW_EFFECT_DURATION = 3000L

    private var velocityY = 0f
    private var screenWidth = 0
    private var screenHeight = 0
    private val windEffectX = 0.5f
    private val windEffectY = 0.2f

    private var lastUpdateTime = 0L
    private val targetFPS = 60f
    private val targetFrameTime = 1000f / targetFPS

    // Wind logic tracking
    private var lastWindChangeTime = 0L
    private var windFadeStartTime = 0L
    private var isWindFading = false
    private var isWindActive = false
    private val WIND_ACTIVE_DURATION = 10_000L
    private val WIND_FADE_DURATION = 5_000L
    private val WIND_PAUSE_DURATION = 10_000L

    // Enemy logic tracking
    var enemyState by mutableStateOf<Pair<Float, Float>>(Pair(0f, 0f))
        private set
    private var lastEnemyUpdateTime = 0L
    private var lastEnemySpawnTime = 0L
    private val ENEMY_UPDATE_INTERVAL = 16L
    private val ENEMY_RESPAWN_DELAY = 3_000L

    // Effects tracking
    private var invincibilityEndTime = 0L
    private var slowEffectEndTime = 0L
    private var boostEffectEndTime = 0L
    private val INVINCIBILITY_DURATION = 3_000L
    private val BOOST_DURATION = 3_000L

    // Items
    val currentItem: StateFlow<Items> = getCurrentItemUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, Items.ChildFirefly)

    var itemsOnScreen = mutableStateListOf<ItemPlayState>()
        private set
    var boostItemsOnScreen = mutableStateListOf<BoostItemState>()
        private set
    val allBoostItems = listOf(mini_item, big_item, immortal)
    private var lastBoostSpawnTime = 0L
    private val boost_countdown = 60_000L
    private var lastItemSpawnTime = 0L
    private val item_countdown = 5_000L

    var preCheckNextLevelUnlocked by mutableStateOf(false)
        private set


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
        viewModelScope.launch {
            preCheckNextLevelUnlocked = checkNextLevelUnlockedUseCase(level.levelNumber)
        }
        viewModelScope.launch {
            _isNextLevelUnlocked.value = checkNextLevelUnlockedUseCase(levelNumber)
        }
        loadBestScore(level)
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

        val currentTime = System.currentTimeMillis()
        lastWindChangeTime = currentTime
        lastEnemySpawnTime = currentTime

        startGameLoop()
    }

    fun restart() {
        gameStatus = GameStatus.Started
        resetGame()

        val currentTime = System.currentTimeMillis()
        lastWindChangeTime = currentTime
        lastEnemySpawnTime = currentTime

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
        levelState = levelState?.copy(firefliesCollected = 0, enemy = null)
        hasWatchedAd = false
        isInvincible = false
        isBig = false
        itemsOnScreen.clear()
        boostItemsOnScreen.clear()
        // Reset wind state
        lastWindChangeTime = 0L
        windFadeStartTime = 0L
        isWindFading = false
        isWindActive = false
        windDirectionToggle = true

        // Reset enemy state
        lastEnemyUpdateTime = 0L
        lastEnemySpawnTime = 0L

        // Reset effects
        invincibilityEndTime = 0L
        slowEffectEndTime = 0L
        isSlowEffectActive = false
        boostEffectEndTime = 0L

        dashCooldownUntil = 0L
        highJumpCooldownUntil = 0L
        slowCooldownUntil = 0L

        lastUpdateTime = 0L

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

            val currentTime = System.currentTimeMillis()
            lastWindChangeTime = currentTime
            lastEnemySpawnTime = currentTime

            startGameLoop()
        }
    }

    private fun activateInvincibility() {
        isInvincible = true
        invincibilityEndTime = System.currentTimeMillis() + INVINCIBILITY_DURATION
    }

    //skill
    fun useDashSkill(): Boolean {
        if (isDashOnCooldown() || gameStatus != GameStatus.Started) return false

        dashCooldownUntil = System.currentTimeMillis() + DASH_COOLDOWN

        val character = characterState ?: return false
        val sortedPipes = pipePairs.sortedBy { it.x }.filter { it.x > character.x }

        val dashDistance = when {
            sortedPipes.size >= 2 -> {
                val secondPipe = sortedPipes[1]
                secondPipe.x + gameConfig.pipeWidth + 50f - character.x
            }

            sortedPipes.size == 1 -> {
                val firstPipe = sortedPipes[0]
                firstPipe.x + gameConfig.pipeWidth + 50f - character.x
            }

            else -> 400f
        }

        pipePairs = pipePairs.map { pipe ->
            pipe.copy(x = pipe.x - dashDistance)
        }

        itemsOnScreen.replaceAll { item ->
            item.copy(x = item.x - dashDistance)
        }

        soundEffectPlayer.playJump()
        return true
    }

    fun useHighJumpSkill(): Boolean {
        val character = characterState ?: return false
        if (isHighJumpOnCooldown() || gameStatus != GameStatus.Started) return false

        highJumpCooldownUntil = System.currentTimeMillis() + HIGH_JUMP_COOLDOWN
        val level = levelState
        velocityY = if (level?.level?.levelNumber == 6)
            -(gameConfig.birdJumpImpulse * 10.0f) - (level.windY * windEffectY)
        else
            (gameConfig.birdJumpImpulse * 10.0f) - (level?.windY ?: 0f) * windEffectY

        if (character.character.name == "Oii") {
            soundEffectPlayer.playOiiJump()
        } else {
            soundEffectPlayer.playJump()
        }
        return true
    }

    fun useSlowSkill(): Boolean {
        if (isSlowOnCooldown() || gameStatus != GameStatus.Started) return false

        slowCooldownUntil = System.currentTimeMillis() + SLOW_COOLDOWN
        isSlowEffectActive = true
        slowEffectEndTime = System.currentTimeMillis() + SLOW_EFFECT_DURATION

        soundEffectPlayer.playJump()
        return true
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
            lastUpdateTime = System.currentTimeMillis()

            while (isActive && gameStatus == GameStatus.Started) {
                val currentTime = System.currentTimeMillis()
                val deltaTime = if (lastUpdateTime == 0L) {
                    1f
                } else {
                    (currentTime - lastUpdateTime) / targetFrameTime
                }
                lastUpdateTime = currentTime

                // Update wind logic
                updateWindLogic(currentTime)

                // Update enemy logic (chỉ cho level 5)
                if (levelState?.level?.levelNumber == 5) {
                    updateEnemyLogic(currentTime)
                }

                // Update invincibility
                updateInvincibilityLogic(currentTime)

                // Update slow effect
                updateSlowEffectLogic(currentTime)

                // Update boost effects
                updateBoostEffectLogic(currentTime)

                // Update game frame
                updateFrame(deltaTime)

                val gameFps = _graphicMode.value.fps
                val frameDelay = if (isSlowEffectActive) {
                    (1000L / gameFps) * 2
                } else {
                    (1000L / gameFps)
                }
                delay(frameDelay)
            }
        }
    }

    private fun updateWindLogic(currentTime: Long) {
        val level = levelState?.level ?: return

        if (!isWindActive && !isWindFading) {
            // Đang trong thời gian nghỉ
            if (currentTime - lastWindChangeTime >= WIND_PAUSE_DURATION) {
                isWindActive = true
                windDirectionToggle = !windDirectionToggle
                updateWind()
                lastWindChangeTime = currentTime
            }
        } else if (isWindActive && !isWindFading) {
            // Đang trong thời gian gió thổi
            if (currentTime - lastWindChangeTime >= WIND_ACTIVE_DURATION) {
                isWindFading = true
                windFadeStartTime = currentTime
            }
        } else if (isWindFading) {
            // Đang trong quá trình giảm dần
            val fadeProgress = (currentTime - windFadeStartTime).toFloat() / WIND_FADE_DURATION

            if (fadeProgress >= 1f) {
                levelState = levelState?.copy(windX = 0f, windY = 0f)
                isWindFading = false
                isWindActive = false
                lastWindChangeTime = currentTime
            } else {
                val fadeMultiplier = 1f - fadeProgress
                val (baseWindX, baseWindY) = getWindForLevel(level.levelNumber)

                var windX = baseWindX * fadeMultiplier
                var windY = baseWindY * fadeMultiplier

                if (level.levelNumber == 2 || level.levelNumber == 3) {
                    windX = if (windDirectionToggle) windX else -windX
                }

                levelState = levelState?.copy(windX = windX, windY = windY)
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

    private fun updateEnemyLogic(currentTime: Long) {
        if (currentTime - lastEnemyUpdateTime < ENEMY_UPDATE_INTERVAL) {
            return
        }
        lastEnemyUpdateTime = currentTime

        val enemy = levelState?.enemy

        if (enemy == null) {
            if (currentTime - lastEnemySpawnTime >= ENEMY_RESPAWN_DELAY) {
                levelState = levelState?.copy(enemy = Enemy.ShishaDog)
                val enemyHeight = Random.nextFloat() * gameConfig.screenHeight
                enemyState = Pair(gameConfig.screenWidth + 200f, enemyHeight)
            }
        } else {
            val newX = enemyState.first - enemy.speed

            if (newX < -200f) {
                levelState = levelState?.copy(enemy = null)
                lastEnemySpawnTime = currentTime
            } else {
                enemyState = Pair(newX, enemyState.second)
            }
        }
    }

    private fun updateInvincibilityLogic(currentTime: Long) {
        if (isInvincible && invincibilityEndTime > 0L && currentTime >= invincibilityEndTime) {
            isInvincible = false
            invincibilityEndTime = 0L
        }
    }

    private fun updateSlowEffectLogic(currentTime: Long) {
        if (isSlowEffectActive && currentTime >= slowEffectEndTime) {
            isSlowEffectActive = false
            slowEffectEndTime = 0L
        }
    }

    private fun updateBoostEffectLogic(currentTime: Long) {
        if (boostEffectEndTime > 0L && currentTime >= boostEffectEndTime) {
            characterState = characterState?.copy(scale = 1f)
            isBig = false
            boostEffectEndTime = 0L
        }
    }

    private fun updateFrame(deltaTime: Float) {
        val character = characterState ?: return
        val level = levelState ?: return

        val gravityEffect =
            if (level.level.levelNumber == 6) -gameConfig.gravity else gameConfig.gravity
        val windEffect =
            if (level.level.levelNumber == 6) -level.windY * windEffectY else level.windY * windEffectY

        velocityY = velocityY + (gravityEffect + windEffect) * deltaTime

        val newX = character.x
        val halfHeight = character.height / 2f
        val newY =
            (character.y + velocityY * deltaTime).coerceIn(halfHeight, screenHeight - halfHeight)

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

        updatePipes(deltaTime)

        val levelWindX = levelState?.windX ?: 0f
        val itemVelocity = (gameConfig.pipeVelocity + levelWindX * windEffectX) * deltaTime

        itemsOnScreen.replaceAll { item ->
            item.copy(x = item.x - itemVelocity)
        }

        itemsOnScreen.removeAll { it.x < -it.item.itemWidth }

        val boostItemVelocity = if (isSlowEffectActive) {
            ((gameConfig.pipeVelocity * 0.3f) + (levelWindX * windEffectX * 0.3f)) * deltaTime
        } else {
            (gameConfig.pipeVelocity + levelWindX * windEffectX) * deltaTime
        }

        boostItemsOnScreen.replaceAll { boost ->
            boost.copy(x = boost.x - boostItemVelocity)
        }

        boostItemsOnScreen.removeAll { it.x < -it.boostItem.width }

        checkItemCollision(character)
        checkBoostItemCollision(character)
        if (level.level.levelNumber == 5) {
            checkEnemyCollision(character)
        }

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
    private fun updatePipes(deltaTime: Float) {
        val levelWindX = levelState?.windX ?: 0f
        val baseVelocity = gameConfig.pipeVelocity

        val pipeVelocity = if (isSlowEffectActive) {
            ((baseVelocity * 0.3f) + levelWindX * windEffectX * 0.3f) * deltaTime
        } else {
            (baseVelocity + levelWindX * windEffectX) * deltaTime
        }

        val updatedPipes = pipePairs.map { pipe ->
            var newBottomHeight = pipe.bottomHeight
            pipe.targetBottomHeight?.let { target ->
                if (newBottomHeight < target) {
                    val riseSpeed = (5f + baseVelocity * 0.5f) * deltaTime
                    newBottomHeight = (newBottomHeight + riseSpeed).coerceAtMost(target)

                    if (levelState?.level?.levelNumber == 2) {
                        val time = System.currentTimeMillis() / 200f
                        val shake = sin(time) * 1f
                        newBottomHeight += shake
                    }
                }
            }

            var newTopHeight = pipe.topHeight
            pipe.targetTopHeight?.let { target ->
                if (newTopHeight < target) {
                    val dropSpeed = (5f + baseVelocity * 0.5f) * deltaTime
                    newTopHeight = (newTopHeight + dropSpeed).coerceAtMost(target)

                    if (levelState?.level?.levelNumber == 2) {
                        val time = System.currentTimeMillis() / 200f
                        val shake = kotlin.math.cos(time) * 1f
                        newTopHeight += shake
                    }
                }
            }

            val gapY = screenHeight - newBottomHeight - gameConfig.pipeGapSize / 2

            val shakeOffset = if (levelState?.level?.levelNumber == 2) {
                (sin(System.currentTimeMillis() / 50.0) * 5).toFloat() * deltaTime
            } else 0f

            pipe.copy(
                x = pipe.x - pipeVelocity + shakeOffset,
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
                spawnItem(previousPipe, newPipe)
                spawnBoostItem(previousPipe, newPipe)
            }
            filteredPipes + newPipe
        } else {
            filteredPipes
        }

        pipePairs = finalPipes
    }

    private fun generateNewPipe(): PipePair {
        val initialPipeX = screenWidth.toFloat() + gameConfig.pipeWidth
        val minPipeHeight = 400f
        val maxTopHeight = screenHeight - gameConfig.pipeGapSize - minPipeHeight
        val finalTopHeight = Random.nextFloat() * (maxTopHeight - minPipeHeight) + minPipeHeight
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

            in listOf(3) -> {
                PipePair(
                    x = initialPipeX,
                    y = finalTopHeight + gameConfig.pipeGapSize / 2,
                    topHeight = 0f,
                    bottomHeight = finalBottomHeight,
                    scored = false,
                    targetTopHeight = finalTopHeight
                )
            }

            in listOf(4) -> {
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

    private fun spawnItem(pipe: PipePair, nextPipe: PipePair) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastItemSpawnTime < item_countdown) {
            return
        }

        lastItemSpawnTime = currentTime

        val itemHeight = currentItem.value.itemHeight
        val minY = itemHeight / 2f
        val maxY = screenHeight - itemHeight / 2f
        val randomY = Random.nextFloat() * (maxY - minY) + minY

        val itemWidth = currentItem.value.itemWidth
        val minX = pipe.x + gameConfig.pipeWidth / 2 + itemWidth / 2
        val maxX = nextPipe.x - gameConfig.pipeWidth / 2 - itemWidth / 2
        val itemX = Random.nextFloat() * (maxX - minX) + minX

        val newItem = ItemPlayState(
            item = currentItem.value,
            x = itemX,
            y = randomY
        )
        itemsOnScreen.add(newItem)
    }

    private fun spawnBoostItem(pipe: PipePair, nextPipe: PipePair) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBoostSpawnTime < boost_countdown) {
            return
        }
        lastBoostSpawnTime = currentTime

        val randomItem = allBoostItems.random()
        val itemHeight = randomItem.height
        val itemWidth = randomItem.width

        val gapTop = pipe.y - gameConfig.pipeGapSize / 2 + itemHeight / 2
        val gapBottom = pipe.y + gameConfig.pipeGapSize / 2 - itemHeight / 2
        val randomY = Random.nextFloat() * (gapBottom - gapTop) + gapTop

        val minX = pipe.x + gameConfig.pipeWidth / 2 + itemWidth / 2
        val maxX = nextPipe.x - gameConfig.pipeWidth / 2 - itemWidth / 2
        val itemX = Random.nextFloat() * (maxX - minX) + minX

        val newItem = BoostItemState(
            boostItem = randomItem,
            x = itemX,
            y = randomY
        )
        boostItemsOnScreen.add(newItem)
    }

    private fun checkItemCollision(character: CharacterPlayState) {
        val collected = mutableListOf<ItemPlayState>()

        val characterRect = Rect(
            left = character.x - character.width / 2f,
            top = character.y - character.height / 2f,
            right = character.x + character.width / 2f,
            bottom = character.y + character.height / 2f
        )

        itemsOnScreen.forEach { item ->
            if (isColliding(characterRect, item.toRect())) {
                collected.add(item)
                levelState = levelState?.copy(
                    firefliesCollected = (levelState?.firefliesCollected ?: 0) + 1
                )
            }
        }

        itemsOnScreen.removeAll(collected)
    }

    private fun checkBoostItemCollision(character: CharacterPlayState) {
        val collected = mutableListOf<BoostItemState>()

        val characterRect = Rect(
            left = character.x - character.width / 2f,
            top = character.y - character.height / 2f,
            right = character.x + character.width / 2f,
            bottom = character.y + character.height / 2f
        )

        boostItemsOnScreen.forEach { item ->
            if (isColliding(characterRect, item.toRect())) {
                collected.add(item)
                applyBoost(item.boostItem)
            }
        }
        boostItemsOnScreen.removeAll(collected)
    }

    private fun isColliding(a: Rect, b: Rect): Boolean {
        return a.left < b.right && a.right > b.left && a.top < b.bottom && a.bottom > b.top
    }

    fun applyBoost(boostItem: BoostItem) {
        when (boostItem.id) {
            1 -> { // Mini
                characterState = characterState?.copy(scale = 0.5f)
            }

            2 -> { // Big
                characterState = characterState?.copy(scale = 2f)
                isBig = true
            }

            3 -> { // Immortal
                isInvincible = true
                invincibilityEndTime = System.currentTimeMillis() + BOOST_DURATION
            }
        }

        boostEffectEndTime = System.currentTimeMillis() + BOOST_DURATION
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
        val halfWidth = if (isBig) (character.width * 0.9) / 2f else (character.width * 0.6) / 2f
        val halfHeight =
            if (isBig) (character.height * 0.9) / 2f else (character.height * 0.75) / 2f

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
            if (levelState?.level?.levelNumber == 5 && !preCheckNextLevelUnlocked) {
                completeGame()
            } else {
                completeLevel()
            }

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
            val unlocked = checkNextLevelUnlockedUseCase(currentLevel.levelNumber)
            _isNextLevelUnlocked.value = unlocked
        }
    }

    private fun completeGame() {
        val currentLevel = levelState?.level ?: return
        val nextLevelNumber = currentLevel.levelNumber + 1
        viewModelScope.launch {
            updateLevelUseCase(Level.getByLevelNumber(nextLevelNumber))
            val unlocked = checkNextLevelUnlockedUseCase(currentLevel.levelNumber)
            _isNextLevelUnlocked.value = unlocked
        }
        gameStatus = GameStatus.Win
    }

    private fun checkEnemyCollision(character: CharacterPlayState) {
        val hitboxPadding = 20f
        val characterRect = Rect(
            left = character.x - character.width / 2f + hitboxPadding,
            top = character.y - character.height / 2f + hitboxPadding,
            right = character.x + character.width / 2f - hitboxPadding,
            bottom = character.y + character.height / 2f - hitboxPadding
        )
        levelState?.enemy?.let { enemy ->
            val enemyRect = Rect(
                left = enemyState.first - enemy.width / 2f + hitboxPadding,
                top = enemyState.second - enemy.height / 2f + hitboxPadding,
                right = enemyState.first + enemy.width / 2f - hitboxPadding,
                bottom = enemyState.second + enemy.height / 2f - hitboxPadding
            )
            if (isColliding(characterRect, enemyRect)) {
                gameOver()
            }
        }
    }

    private fun gameOver() {
        gameStatus = GameStatus.Over
        val character = characterState?.character ?: return
        val level = levelState?.level ?: return
        val levelNumber = level.levelNumber

        loopJob?.cancel()

        isInvincible = false
        isSlowEffectActive = false
        invincibilityEndTime = 0L
        slowEffectEndTime = 0L
        boostEffectEndTime = 0L

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
            addCoinUseCase(currentScore * levelNumber)
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
            2 -> Pair(0f, 0f)
            3 -> Pair(2f, 0f)
            4 -> Pair(2f, 2f)
            5 -> Pair(0f, 1f)
            6 -> Pair(2f, 0f)
            else -> Pair(0f, 0f)
        }
    }

    private val _isNextLevelUnlocked = MutableStateFlow(false)
    val isNextLevelUnlocked: StateFlow<Boolean> = _isNextLevelUnlocked

    fun setCurrentLevel() {
        val newLevel = levelState?.level ?: return
        viewModelScope.launch {
            setCurrentLevelUseCase(Level.getByLevelNumber(newLevel.levelNumber + 1))
        }
    }
}