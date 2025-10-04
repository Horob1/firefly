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

/**
 * ViewModel quản lý game Flappy Bird
 * Xử lý toàn bộ logic game bao gồm:
 * - Vật lý di chuyển nhân vật
 * - Sinh và di chuyển ống nước (pipes)
 * - Hệ thống kỹ năng (dash, high jump, slow)
 * - Hiệu ứng gió, enemy, và các item boost
 * - Tính điểm và lưu best score
 */
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

    // Job quản lý game loop - vòng lặp chính của game
    private var loopJob: Job? = null

    // Cấu hình game (kích thước màn hình, tốc độ, lực hấp dẫn...)
    private var gameConfig = Game()
    val currentGameConfig: Game get() = gameConfig

    // Chế độ đồ họa (LOW/MEDIUM/HIGH) - ảnh hưởng đến FPS
    private val _graphicMode = getGraphicModeUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GraphicMode.LOW
    )
    val graphicMode: StateFlow<GraphicMode> = _graphicMode

    // ===== TRẠNG THÁI GAME =====
    // State nhân vật - vị trí, kích thước, scale
    var characterState by mutableStateOf<CharacterPlayState?>(null)
        private set

    // State level - thông tin level, gió, enemy
    var levelState by mutableStateOf<LevelPlayState?>(null)
        private set

    // Trạng thái game: Idle/Started/Paused/Over/Win/Reviving
    var gameStatus by mutableStateOf(GameStatus.Idle)
        private set

    // Điểm hiện tại trong game
    var currentScore by mutableStateOf(0)
        private set

    // Danh sách các cặp ống (top + bottom pipe)
    var pipePairs by mutableStateOf(listOf<PipePair>())
        private set

    // Toggle để thay đổi hướng gió (trái <-> phải)
    private var windDirectionToggle = true

    // Điểm cao nhất của level hiện tại
    var bestScore by mutableStateOf(0)
        private set

    // Đã xem quảng cáo hồi sinh chưa (chỉ được 1 lần)
    var hasWatchedAd by mutableStateOf(false)
        private set

    // Trạng thái bất tử (từ skill hoặc boost item)
    var isInvincible by mutableStateOf(false)
        private set

    // Trạng thái phóng to (từ boost item)
    private var isBig by mutableStateOf(false)

    // ===== HỆ THỐNG COOLDOWN KỸ NĂNG =====
    private var dashCooldownUntil = 0L        // Thời điểm hết cooldown của Dash
    private var highJumpCooldownUntil = 0L    // Thời điểm hết cooldown của High Jump
    private var slowCooldownUntil = 0L        // Thời điểm hết cooldown của Slow
    private var isSlowEffectActive = false    // Hiệu ứng slow đang active

    // Thời gian cooldown (ms)
    private val DASH_COOLDOWN = 20000L        // 20 giây
    private val HIGH_JUMP_COOLDOWN = 13000L   // 13 giây
    private val SLOW_COOLDOWN = 15000L        // 15 giây
    private val SLOW_EFFECT_DURATION = 3000L  // Hiệu ứng slow kéo dài 3 giây

    // ===== VẬT LÝ GAME =====
    private var velocityY = 0f          // Vận tốc rơi của nhân vật (pixel/frame)
    private var screenWidth = 0         // Chiều rộng màn hình
    private var screenHeight = 0        // Chiều cao màn hình
    private val windEffectX = 0.5f      // Hệ số ảnh hưởng gió theo trục X
    private val windEffectY = 0.2f      // Hệ số ảnh hưởng gió theo trục Y

    // FPS và timing
    private var lastUpdateTime = 0L     // Thời điểm update frame cuối
    private val targetFPS = 60f         // FPS mục tiêu
    private val targetFrameTime = 1000f / targetFPS  // Thời gian 1 frame (ms)

    // ===== HỆ THỐNG GIÓ =====
    // Gió có 3 giai đoạn: Active (thổi mạnh) -> Fade (giảm dần) -> Pause (nghỉ)
    private var lastWindChangeTime = 0L      // Thời điểm thay đổi trạng thái gió
    private var windFadeStartTime = 0L       // Thời điểm bắt đầu fade
    private var isWindFading = false         // Đang trong giai đoạn fade
    private var isWindActive = false         // Gió đang active
    private val WIND_ACTIVE_DURATION = 10_000L   // Thời gian gió thổi (10s)
    private val WIND_FADE_DURATION = 5_000L      // Thời gian fade (5s)
    private val WIND_PAUSE_DURATION = 10_000L    // Thời gian nghỉ (10s)

    // ===== HỆ THỐNG ENEMY (Level 5) =====
    var enemyState by mutableStateOf<Pair<Float, Float>>(Pair(0f, 0f))  // Vị trí enemy (x, y)
        private set
    private var lastEnemyUpdateTime = 0L          // Thời điểm update enemy cuối
    private var lastEnemySpawnTime = 0L           // Thời điểm spawn enemy cuối
    private val ENEMY_UPDATE_INTERVAL = 16L       // Cập nhật enemy mỗi 16ms (~60 FPS)
    private val ENEMY_RESPAWN_DELAY = 3_000L      // Spawn lại enemy sau 3s

    // ===== HỆ THỐNG HIỆU ỨNG =====
    private var invincibilityEndTime = 0L    // Thời điểm hết bất tử
    private var slowEffectEndTime = 0L       // Thời điểm hết hiệu ứng slow
    private var boostEffectEndTime = 0L      // Thời điểm hết boost effect
    private val INVINCIBILITY_DURATION = 3_000L  // Bất tử kéo dài 3s
    private val BOOST_DURATION = 3_000L          // Boost kéo dài 3s

    // ===== HỆ THỐNG ITEMS =====
    // Item thường (firefly) - thu thập để hoàn thành level
    val currentItem: StateFlow<Items> = getCurrentItemUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, Items.ChildFirefly)

    var itemsOnScreen = mutableStateListOf<ItemPlayState>()  // Danh sách item đang hiển thị
        private set

    // Boost items (mini, big, immortal) - cho hiệu ứng đặc biệt
    var boostItemsOnScreen = mutableStateListOf<BoostItemState>()
        private set
    val allBoostItems = listOf(mini_item, big_item, immortal)

    private var lastBoostSpawnTime = 0L           // Thời điểm spawn boost item cuối
    private val boost_countdown = 60_000L         // Spawn boost mỗi 60s
    private var lastItemSpawnTime = 0L            // Thời điểm spawn item cuối
    private val item_countdown = 5_000L           // Spawn item mỗi 5s

    // Kiểm tra level tiếp theo đã unlock chưa ngay khi bắt đầu game
    var preCheckNextLevelUnlocked by mutableStateOf(false)
        private set

    var claimFullItem by mutableStateOf(false)

    /**
     * Khởi tạo game với nhân vật và level được chọn
     * @param characterName Tên nhân vật (vd: "Oii", "Bird"...)
     * @param levelNumber Số thứ tự level (1-6)
     */
    fun initializeGame(characterName: String, levelNumber: Int) {
        val character = Character.getByName(characterName)
        val level = Level.getByLevelNumber(levelNumber)

        // Khởi tạo vị trí nhân vật ở 1/4 màn hình, giữa chiều cao
        characterState = CharacterPlayState(
            character = character,
            x = (screenWidth / 4).toFloat(),
            y = (screenHeight / 2).toFloat(),
            width = character.frameWidth.toFloat(),
            height = character.frameHeight.toFloat(),
        )

        // Lấy thông số gió cho level
        val (windX, windY) = getWindForLevel(level.levelNumber)
        levelState = LevelPlayState(
            level = level,
            windX = windX,
            windY = windY
        )

        // Kiểm tra level tiếp theo đã mở khóa chưa
        viewModelScope.launch {
            preCheckNextLevelUnlocked = checkNextLevelUnlockedUseCase(level.levelNumber)
        }
        viewModelScope.launch {
            _isNextLevelUnlocked.value = checkNextLevelUnlockedUseCase(levelNumber)
        }

        // Load điểm cao nhất
        loadBestScore(level)
    }

    /**
     * Cập nhật kích thước màn hình
     * Được gọi khi màn hình thay đổi (xoay ngang/dọc, resize...)
     */
    fun updateScreenSize(width: Int, height: Int) {
        if (screenWidth != width || screenHeight != height) {
            screenWidth = width
            screenHeight = height
            gameConfig = gameConfig.copy(
                screenWidth = screenWidth,
                screenHeight = screenHeight
            )

            // Reset vị trí nhân vật về giữa màn hình
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

    /**
     * Bắt đầu game lần đầu
     */
    fun start() {
        if (gameStatus == GameStatus.Started) return
        gameStatus = GameStatus.Started
        resetGame()

        val currentTime = System.currentTimeMillis()
        lastWindChangeTime = currentTime
        lastEnemySpawnTime = currentTime

        startGameLoop()
    }

    /**
     * Chơi lại game (sau khi game over)
     */
    fun restart() {
        gameStatus = GameStatus.Started
        resetGame()

        val currentTime = System.currentTimeMillis()
        lastWindChangeTime = currentTime
        lastEnemySpawnTime = currentTime

        startGameLoop()
    }

    /**
     * Tạm dừng game
     */
    fun pauseGame() {
        if (gameStatus == GameStatus.Started) {
            gameStatus = GameStatus.Paused
            loopJob?.cancel()  // Dừng game loop
        }
    }

    /**
     * Tiếp tục game sau khi pause
     */
    fun resumeGame() {
        if (gameStatus == GameStatus.Paused) {
            gameStatus = GameStatus.Started
            startGameLoop()  // Khởi động lại game loop
        }
    }

    /**
     * Reset toàn bộ trạng thái game về ban đầu
     */
    private fun resetGame() {
        claimFullItem = false
        // Reset điểm và vật lý
        currentScore = 0
        velocityY = 0f
        pipePairs = emptyList()
        levelState = levelState?.copy(firefliesCollected = 0, enemy = null)

        // Reset flags
        hasWatchedAd = false
        isInvincible = false
        isBig = false

        // Xóa items
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

        // Reset cooldowns
        dashCooldownUntil = 0L
        highJumpCooldownUntil = 0L
        slowCooldownUntil = 0L

        lastUpdateTime = 0L

        // Reset vị trí nhân vật
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

    /**
     * Hồi sinh nhân vật sau khi xem quảng cáo
     * Chỉ được phép 1 lần
     */
    fun revive() {
        val character = characterState?.character ?: return
        hasWatchedAd = true
        gameStatus = GameStatus.Reviving

        // Reset vị trí nhân vật
        characterState = CharacterPlayState(
            character = character,
            x = (screenWidth / 4).toFloat(),
            y = (screenHeight / 2).toFloat(),
            width = character.frameWidth.toFloat(),
            height = character.frameHeight.toFloat(),
        )

        velocityY = 0f

        // Chờ 3 giây rồi tiếp tục game với bất tử
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

    /**
     * Kích hoạt trạng thái bất tử trong 3 giây
     */
    private fun activateInvincibility() {
        isInvincible = true
        invincibilityEndTime = System.currentTimeMillis() + INVINCIBILITY_DURATION
    }

    // ===== KỸ NĂNG (SKILLS) =====

    /**
     * Kỹ năng Dash - lướt qua 1-2 ống tiếp theo
     * Cooldown: 20 giây
     * @return true nếu sử dụng thành công
     */
    fun useDashSkill(): Boolean {
        if (isDashOnCooldown() || gameStatus != GameStatus.Started) return false

        dashCooldownUntil = System.currentTimeMillis() + DASH_COOLDOWN

        val character = characterState ?: return false

        // Tìm các ống phía trước nhân vật
        val sortedPipes = pipePairs.sortedBy { it.x }.filter { it.x > character.x }

        // Tính khoảng cách dash (vượt qua 2 ống nếu có)
        val dashDistance = when {
            sortedPipes.size >= 2 -> {
                val secondPipe = sortedPipes[1]
                secondPipe.x + gameConfig.pipeWidth + 50f - character.x
            }

            sortedPipes.size == 1 -> {
                val firstPipe = sortedPipes[0]
                firstPipe.x + gameConfig.pipeWidth + 50f - character.x
            }

            else -> 400f  // Khoảng cách mặc định
        }

        // Dịch chuyển các ống về phía sau (tạo hiệu ứng nhân vật lướt)
        pipePairs = pipePairs.map { pipe ->
            pipe.copy(x = pipe.x - dashDistance)
        }

        // Dịch chuyển items
        itemsOnScreen.replaceAll { item ->
            item.copy(x = item.x - dashDistance)
        }

        soundEffectPlayer.playJump()
        return true
    }

    /**
     * Kỹ năng High Jump - nhảy cao gấp 10 lần
     * Cooldown: 13 giây
     * Level 6 (ngược trọng lực) cần đảo chiều vận tốc
     * @return true nếu sử dụng thành công
     */
    fun useHighJumpSkill(): Boolean {
        val character = characterState ?: return false
        if (isHighJumpOnCooldown() || gameStatus != GameStatus.Started) return false

        highJumpCooldownUntil = System.currentTimeMillis() + HIGH_JUMP_COOLDOWN
        val level = levelState

        // Level 6 có trọng lực ngược nên cần đảo chiều
        velocityY = if (level?.level?.levelNumber == 6)
            -(gameConfig.birdJumpImpulse * 10.0f) - (level.windY * windEffectY)
        else
            (gameConfig.birdJumpImpulse * 10.0f) - (level?.windY ?: 0f) * windEffectY

        // Nhân vật Oii có âm thanh riêng
        if (character.character.name == "Oii") {
            soundEffectPlayer.playOiiJump()
        } else {
            soundEffectPlayer.playJump()
        }
        return true
    }

    /**
     * Kỹ năng Slow - làm chậm tốc độ game trong 3 giây
     * Cooldown: 15 giây
     * @return true nếu sử dụng thành công
     */
    fun useSlowSkill(): Boolean {
        if (isSlowOnCooldown() || gameStatus != GameStatus.Started) return false

        slowCooldownUntil = System.currentTimeMillis() + SLOW_COOLDOWN
        isSlowEffectActive = true
        slowEffectEndTime = System.currentTimeMillis() + SLOW_EFFECT_DURATION

        soundEffectPlayer.playJump()
        return true
    }

    // Các hàm check cooldown
    fun isDashOnCooldown(): Boolean {
        return System.currentTimeMillis() < dashCooldownUntil
    }

    fun isHighJumpOnCooldown(): Boolean {
        return System.currentTimeMillis() < highJumpCooldownUntil
    }

    fun isSlowOnCooldown(): Boolean {
        return System.currentTimeMillis() < slowCooldownUntil
    }

    /**
     * Game Loop chính - chạy liên tục mỗi frame
     * Xử lý:
     * 1. Logic gió
     * 2. Logic enemy (level 5)
     * 3. Logic bất tử, slow, boost
     * 4. Update frame game
     * 5. Delay theo FPS
     */
    private fun startGameLoop() {
        loopJob?.cancel()
        loopJob = viewModelScope.launch {
            lastUpdateTime = System.currentTimeMillis()

            while (isActive && gameStatus == GameStatus.Started) {
                val currentTime = System.currentTimeMillis()

                // Tính deltaTime để đảm bảo chuyển động mượt
                val deltaTime = if (lastUpdateTime == 0L) {
                    1f
                } else {
                    (currentTime - lastUpdateTime) / targetFrameTime
                }
                lastUpdateTime = currentTime

                // Update các logic game
                updateWindLogic(currentTime)

                if (levelState?.level?.levelNumber == 5) {
                    updateEnemyLogic(currentTime)
                }

                updateInvincibilityLogic(currentTime)
                updateSlowEffectLogic(currentTime)
                updateBoostEffectLogic(currentTime)

                // Update frame chính
                updateFrame(deltaTime)

                // Delay theo FPS setting (x2 nếu slow active)
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

    /**
     * Update logic gió - chu kỳ: Active -> Fade -> Pause
     * - Active (10s): Gió thổi mạnh
     * - Fade (5s): Gió giảm dần
     * - Pause (10s): Gió nghỉ
     */
    private fun updateWindLogic(currentTime: Long) {
        val level = levelState?.level ?: return

        if (!isWindActive && !isWindFading) {
            // Đang trong thời gian nghỉ
            if (currentTime - lastWindChangeTime >= WIND_PAUSE_DURATION) {
                isWindActive = true
                windDirectionToggle = !windDirectionToggle  // Đổi hướng gió
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
            // Đang trong quá trình giảm dần (fade out)
            val fadeProgress = (currentTime - windFadeStartTime).toFloat() / WIND_FADE_DURATION

            if (fadeProgress >= 1f) {
                // Hết fade, reset về 0
                levelState = levelState?.copy(windX = 0f, windY = 0f)
                isWindFading = false
                isWindActive = false
                lastWindChangeTime = currentTime
            } else {
                // Giảm dần theo progress
                val fadeMultiplier = 1f - fadeProgress
                val (baseWindX, baseWindY) = getWindForLevel(level.levelNumber)

                var windX = baseWindX * fadeMultiplier
                var windY = baseWindY * fadeMultiplier

                // Level 2, 3: gió đổi chiều
                if (level.levelNumber == 2 || level.levelNumber == 3) {
                    windX = if (windDirectionToggle) windX else -windX
                }

                levelState = levelState?.copy(windX = windX, windY = windY)
            }
        }
    }

    /**
     * Cập nhật gió với độ mạnh đầy đủ
     */
    private fun updateWind() {
        val level = levelState?.level ?: return
        var (windX, windY) = getWindForLevel(level.levelNumber)

        // Level 2, 3: gió thay đổi chiều trái/phải
        if (level.levelNumber == 2 || level.levelNumber == 3) {
            windX = if (windDirectionToggle) windX else -windX
        }

        levelState = levelState?.copy(windX = windX, windY = windY)
    }

    /**
     * Update logic enemy cho level 5
     * Enemy xuất hiện từ phải, di chuyển sang trái
     * Respawn sau 3s khi ra khỏi màn hình
     */
    private fun updateEnemyLogic(currentTime: Long) {
        // Update mỗi 16ms (60 FPS)
        if (currentTime - lastEnemyUpdateTime < ENEMY_UPDATE_INTERVAL) {
            return
        }
        lastEnemyUpdateTime = currentTime

        val enemy = levelState?.enemy

        if (enemy == null) {
            // Chưa có enemy, spawn mới sau delay
            if (currentTime - lastEnemySpawnTime >= ENEMY_RESPAWN_DELAY) {
                levelState = levelState?.copy(enemy = Enemy.ShishaDog)
                val enemyHeight = Random.nextFloat() * gameConfig.screenHeight
                enemyState = Pair(gameConfig.screenWidth + 200f, enemyHeight)
            }
        } else {
            // Di chuyển enemy sang trái
            val newX = enemyState.first - enemy.speed

            if (newX < -200f) {
                // Enemy ra khỏi màn hình, xóa
                levelState = levelState?.copy(enemy = null)
                lastEnemySpawnTime = currentTime
            } else {
                enemyState = Pair(newX, enemyState.second)
            }
        }
    }

    /**
     * Kiểm tra và tắt bất tử khi hết thời gian
     */
    private fun updateInvincibilityLogic(currentTime: Long) {
        if (isInvincible && invincibilityEndTime > 0L && currentTime >= invincibilityEndTime) {
            isInvincible = false
            invincibilityEndTime = 0L
        }
    }

    /**
     * Kiểm tra và tắt hiệu ứng slow khi hết thời gian
     */
    private fun updateSlowEffectLogic(currentTime: Long) {
        if (isSlowEffectActive && currentTime >= slowEffectEndTime) {
            isSlowEffectActive = false
            slowEffectEndTime = 0L
        }
    }

    /**
     * Kiểm tra và reset kích thước nhân vật khi hết boost
     */
    private fun updateBoostEffectLogic(currentTime: Long) {
        if (boostEffectEndTime > 0L && currentTime >= boostEffectEndTime) {
            characterState = characterState?.copy(scale = 1f)  // Reset về kích thước bình thường
            isBig = false
            boostEffectEndTime = 0L
        }
    }

    /**
     * Update frame chính của game
     * Xử lý:
     * 1. Vật lý rơi (gravity + wind)
     * 2. Di chuyển nhân vật
     * 3. Kiểm tra va chạm với biên
     * 4. Update pipes và items
     * 5. Kiểm tra collision
     * 6. Update điểm
     *
     * @param deltaTime Thời gian trôi qua từ frame trước (normalized theo 60 FPS)
     */
    private fun updateFrame(deltaTime: Float) {
        val character = characterState ?: return
        val level = levelState ?: return

        // Tính toán gravity (level 6 có trọng lực ngược)
        val gravityEffect =
            if (level.level.levelNumber == 6) -gameConfig.gravity else gameConfig.gravity

        // Tính toán ảnh hưởng của gió
        val windEffect =
            if (level.level.levelNumber == 6) -level.windY * windEffectY else level.windY * windEffectY

        // Update vận tốc rơi: v = v + (g + wind) * deltaTime
        velocityY = velocityY + (gravityEffect + windEffect) * deltaTime

        // Update vị trí nhân vật
        val newX = character.x
        val halfHeight = character.height / 2f
        // Giới hạn nhân vật không bay ra khỏi màn hình
        val newY =
            (character.y + velocityY * deltaTime).coerceIn(halfHeight, screenHeight - halfHeight)

        characterState = character.copy(x = newX, y = newY)

        // Kiểm tra va chạm với biên màn hình (game over)
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

        // Update pipes
        updatePipes(deltaTime)

        // Update vị trí items thường (firefly)
        val levelWindX = levelState?.windX ?: 0f
        val itemVelocity = (gameConfig.pipeVelocity + levelWindX * windEffectX) * deltaTime

        itemsOnScreen.replaceAll { item ->
            item.copy(x = item.x - itemVelocity)
        }

        // Xóa items đã ra khỏi màn hình
        itemsOnScreen.removeAll { it.x < -it.item.itemWidth }

        // Update boost items (chậm hơn 70% khi slow active)
        val boostItemVelocity = if (isSlowEffectActive) {
            ((gameConfig.pipeVelocity * 0.3f) + (levelWindX * windEffectX * 0.3f)) * deltaTime
        } else {
            (gameConfig.pipeVelocity + levelWindX * windEffectX) * deltaTime
        }

        boostItemsOnScreen.replaceAll { boost ->
            boost.copy(x = boost.x - boostItemVelocity)
        }

        boostItemsOnScreen.removeAll { it.x < -it.boostItem.width }

        // Kiểm tra thu thập items
        checkItemCollision(character)
        checkBoostItemCollision(character)

        // Kiểm tra va chạm với enemy (chỉ level 5)
        if (level.level.levelNumber == 5) {
            checkEnemyCollision(character)
        }

        // Kiểm tra collision với pipes (nếu không bất tử)
        if (!isInvincible && checkCollisions()) {
            gameOver()
            return
        }

        // Update điểm
        updateScore()
    }

    /**
     * Nhân vật nhảy (tap màn hình)
     * Level 6 có trọng lực ngược nên đảo chiều impulse
     */
    fun jump() {
        if (gameStatus != GameStatus.Started) return
        val level = levelState

        // Set vận tốc nhảy (âm = lên, dương = xuống)
        velocityY = if (level?.level?.levelNumber == 6)
            -gameConfig.birdJumpImpulse - level.windY * windEffectY
        else
            gameConfig.birdJumpImpulse - (level?.windY ?: 0f) * windEffectY

        soundEffectPlayer.playJump()
    }

    /**
     * Kiểm tra có thể xem quảng cáo hồi sinh không
     */
    fun canWatchReviveAd(): Boolean {
        return !hasWatchedAd && gameStatus == GameStatus.Over
    }

    // ===== QUẢN LÝ PIPES =====

    /**
     * Update vị trí và trạng thái của tất cả pipes
     * Xử lý:
     * 1. Di chuyển pipes sang trái
     * 2. Animate pipes mọc lên (level 3,4,5,6)
     * 3. Shake effect (level 2)
     * 4. Xóa pipes ra khỏi màn hình
     * 5. Spawn pipe mới khi cần
     */
    private fun updatePipes(deltaTime: Float) {
        val levelWindX = levelState?.windX ?: 0f
        val baseVelocity = gameConfig.pipeVelocity

        // Tính vận tốc pipe (chậm hơn 70% khi slow active)
        val pipeVelocity = if (isSlowEffectActive) {
            ((baseVelocity * 0.3f) + levelWindX * windEffectX * 0.3f) * deltaTime
        } else {
            (baseVelocity + levelWindX * windEffectX) * deltaTime
        }

        // Update từng pipe
        val updatedPipes = pipePairs.map { pipe ->
            // Animate bottom pipe mọc lên (level 3,4,5,6)
            var newBottomHeight = pipe.bottomHeight
            pipe.targetBottomHeight?.let { target ->
                if (newBottomHeight < target) {
                    val riseSpeed = (5f + baseVelocity * 0.5f) * deltaTime
                    newBottomHeight = (newBottomHeight + riseSpeed).coerceAtMost(target)

                    // Level 2: thêm shake effect
                    if (levelState?.level?.levelNumber == 2) {
                        val time = System.currentTimeMillis() / 200f
                        val shake = sin(time) * 1f
                        newBottomHeight += shake
                    }
                }
            }

            // Animate top pipe rơi xuống (level 5,6)
            var newTopHeight = pipe.topHeight
            pipe.targetTopHeight?.let { target ->
                if (newTopHeight < target) {
                    val dropSpeed = (5f + baseVelocity * 0.5f) * deltaTime
                    newTopHeight = (newTopHeight + dropSpeed).coerceAtMost(target)

                    // Level 2: shake effect với cos
                    if (levelState?.level?.levelNumber == 2) {
                        val time = System.currentTimeMillis() / 200f
                        val shake = kotlin.math.cos(time) * 1f
                        newTopHeight += shake
                    }
                }
            }

            // Tính vị trí Y của gap (khoảng trống giữa 2 pipes)
            val gapY = screenHeight - newBottomHeight - gameConfig.pipeGapSize / 2

            // Level 2: thêm shake ngang
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

        // Lọc bỏ pipes đã ra khỏi màn hình
        val filteredPipes = updatedPipes.filter {
            it.x + gameConfig.pipeWidth > 0
        }

        // Spawn pipe mới khi cần (khi pipe cuối ở giữa màn hình)
        val finalPipes = if (filteredPipes.isEmpty() || filteredPipes.last().x < screenWidth / 2) {
            val newPipe = generateNewPipe()
            val previousPipe = filteredPipes.lastOrNull()
            if (previousPipe != null) {
                // Spawn items giữa 2 pipes
                if (!claimFullItem) {
                    spawnItem(previousPipe, newPipe)
                }
                spawnBoostItem(previousPipe, newPipe)
            }
            filteredPipes + newPipe
        } else {
            filteredPipes
        }

        pipePairs = finalPipes
    }

    /**
     * Tạo pipe mới với độ cao ngẫu nhiên
     * Mỗi level có cách sinh pipe khác nhau:
     * - Level 1,2: Pipes đầy đủ ngay từ đầu
     * - Level 3: Chỉ bottom pipe, top pipe mọc sau
     * - Level 4: Chỉ top pipe, bottom pipe mọc sau
     * - Level 5,6: Cả 2 pipes đều mọc từ 0
     */
    private fun generateNewPipe(): PipePair {
        val initialPipeX = screenWidth.toFloat() + gameConfig.pipeWidth
        val minPipeHeight = 400f
        val maxTopHeight = screenHeight - gameConfig.pipeGapSize - minPipeHeight

        // Random chiều cao final của pipes
        val finalTopHeight = Random.nextFloat() * (maxTopHeight - minPipeHeight) + minPipeHeight
        val finalBottomHeight = screenHeight - finalTopHeight - gameConfig.pipeGapSize

        return when (levelState?.level?.levelNumber) {
            in listOf(5, 6) -> {
                // Cả 2 pipes mọc từ 0
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
                // Chỉ bottom pipe có sẵn, top pipe mọc sau
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
                // Chỉ top pipe có sẵn, bottom pipe mọc sau
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
                // Level 1,2: Pipes đầy đủ ngay từ đầu
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

    /**
     * Spawn item thường (firefly) giữa 2 pipes
     * Spawn mỗi 5 giây
     */
    private fun spawnItem(pipe: PipePair, nextPipe: PipePair) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastItemSpawnTime < item_countdown) {
            return
        }

        lastItemSpawnTime = currentTime

        // Random vị trí Y trong màn hình
        val itemHeight = currentItem.value.itemHeight
        val minY = itemHeight / 2f
        val maxY = screenHeight - itemHeight / 2f
        val randomY = Random.nextFloat() * (maxY - minY) + minY

        // Random vị trí X giữa 2 pipes
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

    /**
     * Spawn boost item (mini/big/immortal) trong gap giữa pipes
     * Spawn mỗi 60 giây
     */
    private fun spawnBoostItem(pipe: PipePair, nextPipe: PipePair) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBoostSpawnTime < boost_countdown) {
            return
        }
        lastBoostSpawnTime = currentTime

        // Random 1 trong 3 loại boost
        val randomItem = allBoostItems.random()
        val itemHeight = randomItem.height
        val itemWidth = randomItem.width

        // Random Y trong khoảng gap giữa 2 pipes
        val gapTop = pipe.y - gameConfig.pipeGapSize / 2 + itemHeight / 2
        val gapBottom = pipe.y + gameConfig.pipeGapSize / 2 - itemHeight / 2
        val randomY = Random.nextFloat() * (gapBottom - gapTop) + gapTop

        // Random X giữa 2 pipes
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

    /**
     * Kiểm tra và xử lý thu thập item thường (firefly)
     */
    private fun checkItemCollision(character: CharacterPlayState) {
        val collected = mutableListOf<ItemPlayState>()

        // Tạo hitbox nhân vật
        val characterRect = Rect(
            left = character.x - character.width / 2f,
            top = character.y - character.height / 2f,
            right = character.x + character.width / 2f,
            bottom = character.y + character.height / 2f
        )

        // Kiểm tra collision với từng item
        itemsOnScreen.forEach { item ->
            if (isColliding(characterRect, item.toRect())) {
                collected.add(item)
                // Tăng số lượng firefly đã thu thập
                levelState = levelState?.copy(
                    firefliesCollected = (levelState?.firefliesCollected ?: 0) + 1
                )
            }
        }

        // Xóa items đã thu thập
        itemsOnScreen.removeAll(collected)
    }

    /**
     * Kiểm tra và xử lý thu thập boost item
     */
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
                applyBoost(item.boostItem)  // Áp dụng hiệu ứng boost
            }
        }
        boostItemsOnScreen.removeAll(collected)
    }

    /**
     * Kiểm tra 2 hình chữ nhật có giao nhau không (AABB collision)
     */
    private fun isColliding(a: Rect, b: Rect): Boolean {
        return a.left < b.right && a.right > b.left && a.top < b.bottom && a.bottom > b.top
    }

    /**
     * Áp dụng hiệu ứng boost khi thu thập
     * - Mini (id=1): Thu nhỏ nhân vật 50%
     * - Big (id=2): Phóng to nhân vật 200%
     * - Immortal (id=3): Bất tử trong 3 giây
     */
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

    // ===== HỆ THỐNG ĐIỂM SỐ =====

    /**
     * Load best score của level từ database
     */
    fun loadBestScore(level: Level) {
        viewModelScope.launch {
            bestScore = getBestScoreUseCase(level)
        }
    }

    /**
     * Kiểm tra collision với tất cả pipes
     * @return true nếu có va chạm
     */
    private fun checkCollisions(): Boolean {
        val character = characterState ?: return false

        pipePairs.forEach { pipe ->
            if (isCollisionWithPipe(character, pipe)) {
                return true
            }
        }
        return false
    }

    /**
     * Kiểm tra collision giữa nhân vật và 1 pipe
     * Sử dụng hitbox nhỏ hơn sprite thật để dễ chơi hơn
     * - Nhân vật thường: 60% width, 75% height
     * - Nhân vật big: 90% width, 90% height
     */
    private fun isCollisionWithPipe(character: CharacterPlayState, pipe: PipePair): Boolean {
        // Tính hitbox nhân vật (nhỏ hơn sprite thật)
        val halfWidth = if (isBig) (character.width * 0.9) / 2f else (character.width * 0.6) / 2f
        val halfHeight =
            if (isBig) (character.height * 0.9) / 2f else (character.height * 0.75) / 2f

        val birdLeft = character.x - halfWidth
        val birdRight = character.x + halfWidth
        val birdTop = character.y - halfHeight
        val birdBottom = character.y + halfHeight

        // Hitbox pipe
        val pipeLeft = pipe.x - gameConfig.pipeWidth / 2
        val pipeRight = pipe.x + gameConfig.pipeWidth / 2

        // Kiểm tra collision ngang
        val horizontalCollision = birdRight > pipeLeft && birdLeft < pipeRight

        if (horizontalCollision) {
            val currentTopPipeHeight = pipe.topHeight
            val currentBottomPipeHeight = pipe.bottomHeight

            // Va chạm với top pipe
            if (currentTopPipeHeight > 0 && birdTop < currentTopPipeHeight) {
                return true
            }

            // Va chạm với bottom pipe
            val bottomPipeTop = screenHeight - currentBottomPipeHeight
            if (currentBottomPipeHeight > 0 && birdBottom > bottomPipeTop) {
                return true
            }

            // Level 6: va chạm với biên cũng được tính
            if (levelState?.level?.levelNumber == 6) {
                if (birdTop <= 0 || birdBottom >= screenHeight) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Update điểm số
     * - Cộng điểm khi vượt qua pipe
     * - Kiểm tra hoàn thành level (đủ firefly)
     * - Kiểm tra hoàn thành game (level 5 + unlock next level)
     */
    private fun updateScore() {
        val character = characterState ?: return

        // Đánh dấu pipe đã scored khi nhân vật vượt qua
        val updatedPipes = pipePairs.map { pipe ->
            if (!pipe.scored && character.x > pipe.x + gameConfig.pipeWidth / 2) {
                currentScore += 1
                pipe.copy(scored = true)
            } else {
                pipe
            }
        }

        pipePairs = updatedPipes

        // Kiểm tra hoàn thành level
        if ((levelState?.firefliesCollected ?: 0) >= (levelState?.firefliesRequired ?: 0)) {
            if (levelState?.level?.levelNumber == 5 && !preCheckNextLevelUnlocked) {
                completeGame()  // Level 5 lần đầu = hoàn thành game
            } else {
                completeLevel()  // Các level khác = unlock level tiếp
            }
        }
    }

    /**
     * Lưu best score vào database
     */
    fun saveBestScore(level: Level, score: Int) {
        viewModelScope.launch {
            updatePlaySectionUseCase(level, score)
        }
    }

    /**
     * Hoàn thành level - unlock level tiếp theo
     */
    private fun completeLevel() {
        claimFullItem = true
        val currentLevel = levelState?.level ?: return
        val nextLevelNumber = currentLevel.levelNumber + 1
        viewModelScope.launch {
            updateLevelUseCase(Level.getByLevelNumber(nextLevelNumber))
            val unlocked = checkNextLevelUnlockedUseCase(currentLevel.levelNumber)
            _isNextLevelUnlocked.value = unlocked
        }
    }

    /**
     * Hoàn thành game (level 5 lần đầu) - hiển thị màn Win
     */
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

    /**
     * Kiểm tra collision với enemy (level 5)
     * Sử dụng padding để hitbox nhỏ hơn sprite
     */
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

    /**
     * Xử lý game over
     * - Dừng game loop
     * - Reset các effects
     * - Phát âm thanh
     * - Lưu best score
     * - Thưởng coin (score * levelNumber)
     */
    private fun gameOver() {
        gameStatus = GameStatus.Over
        val character = characterState?.character ?: return
        val level = levelState?.level ?: return
        val levelNumber = level.levelNumber

        loopJob?.cancel()

        // Reset tất cả effects
        isInvincible = false
        isSlowEffectActive = false
        invincibilityEndTime = 0L
        slowEffectEndTime = 0L
        boostEffectEndTime = 0L

        // Phát âm thanh game over (nhân vật Oii có âm riêng)
        if (character.name == "Oii") {
            soundEffectPlayer.playOiiOver()
        } else {
            soundEffectPlayer.playGameOver()
        }

        // Lưu best score nếu phá kỷ lục
        val currentLevel = levelState?.level ?: return
        if (currentScore > bestScore) {
            saveBestScore(currentLevel, currentScore)
            bestScore = currentScore
        }

        // Thưởng coin = score * levelNumber
        viewModelScope.launch {
            addCoinUseCase(currentScore * levelNumber)
        }
    }

    /**
     * Tính góc nghiêng của nhân vật dựa trên vận tốc rơi
     * Rơi nhanh -> nghiêng xuống 30 độ
     */
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

    // ===== ÂM THANH VÀ NHẠC NỀN =====

    // Settings nhạc nền
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

    // Settings sound effect
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

    // Lắng nghe thay đổi sound effect settings
    init {
        viewModelScope.launch {
            soundEffectSettings.collect { settings ->
                soundEffectPlayer.sfxEnabled = settings.isEnabled
                soundEffectPlayer.sfxVolume = settings.volume
            }
        }
    }

    /**
     * Lấy thông số gió cho từng level
     * @return Pair(windX, windY) - độ mạnh gió theo trục X và Y
     *
     * Level 1: Không có gió
     * Level 2: Không có gió (nhưng có shake effect)
     * Level 3: Gió ngang (đổi chiều)
     * Level 4: Gió chéo (ngang + dọc)
     * Level 5: Gió dọc
     * Level 6: Gió ngang (trọng lực ngược)
     */
    fun getWindForLevel(levelNumber: Int): Pair<Float, Float> {
        return when (levelNumber) {
            1 -> Pair(0f, 0f)      // Không gió
            2 -> Pair(0f, 0f)      // Không gió (có shake)
            3 -> Pair(2f, 0f)      // Gió ngang (đổi chiều trái/phải)
            4 -> Pair(2f, 2f)      // Gió chéo (ngang + dọc)
            5 -> Pair(0f, 1f)      // Gió dọc (đẩy lên)
            6 -> Pair(2f, 0f)      // Gió ngang (trọng lực ngược)
            else -> Pair(0f, 0f)
        }
    }

    // StateFlow kiểm tra level tiếp theo đã unlock chưa
    private val _isNextLevelUnlocked = MutableStateFlow(false)
    val isNextLevelUnlocked: StateFlow<Boolean> = _isNextLevelUnlocked

    /**
     * Set level hiện tại thành level tiếp theo
     * Được gọi khi người chơi muốn chuyển sang level mới
     */
    fun setCurrentLevel() {
        val newLevel = levelState?.level ?: return
        viewModelScope.launch {
            setCurrentLevelUseCase(Level.getByLevelNumber(newLevel.levelNumber + 1))
        }
    }
}