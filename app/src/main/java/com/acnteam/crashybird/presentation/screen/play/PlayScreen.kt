package com.acnteam.crashybird.presentation.screen.play

import PauseDialog
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.game.GameStatus
import com.acnteam.crashybird.domain.model.game.Skill
import com.acnteam.crashybird.presentation.common.CrashyBirdButton
import com.acnteam.crashybird.presentation.common.SkillBadge
import com.acnteam.crashybird.presentation.common.sprite.component.drawSpriteView
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSheet
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSpec
import com.acnteam.crashybird.presentation.common.sprite.domain.rememberSpriteState
import com.acnteam.crashybird.presentation.navigation.NavScreen
import com.acnteam.crashybird.presentation.screen.home.Banner
import com.acnteam.crashybird.presentation.screen.play.component.BubbleEffect
import com.acnteam.crashybird.presentation.screen.play.component.FirefliesUI
import com.acnteam.crashybird.presentation.screen.play.component.GameStatusOverlay
import com.acnteam.crashybird.presentation.screen.play.component.RainEffect
import com.acnteam.crashybird.presentation.screen.play.component.WindEffect
import com.acnteam.crashybird.presentation.screen.play.component.vibrateFor5s
import com.acnteam.crashybird.presentation.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.sqrt

const val PIPE_CAP_HEIGHT = 50f

@SuppressLint("LocalContextResourcesRead")
@Composable
fun PlayScreen(
    levelNumber: Int,
    characterName: String,
    viewModel: PlayViewModel,
    navController: NavController,
) {

    val downWindImage: ImageBitmap = ImageBitmap.imageResource(id = R.drawable.down_wind)
    val crossWindImage: ImageBitmap = ImageBitmap.imageResource(id = R.drawable.cross_wind)
    val characterState = viewModel.characterState
    val gameStatus = viewModel.gameStatus
    val currentScore = viewModel.currentScore
    var rain by remember { mutableStateOf(false) }
    var wind by remember { mutableStateOf(false) }
    var screenHeight by remember { mutableIntStateOf(0) }
    var screenWidth by remember { mutableIntStateOf(0) }
    var showPauseDialog by remember { mutableStateOf(false) }
    var showSkillEffect by remember { mutableStateOf<String?>(null) }
    val toastMessage by viewModel.toastMessage.collectAsState()
    val context = LocalContext.current

    val character = Character.getByName(characterName)
    val level = Level.getByLevelNumber(levelNumber)
    val levelState = viewModel.levelState
    val lifecycleOwner = LocalLifecycleOwner.current
    val totalFirefly = levelState?.firefliesRequired
    val firefliesCollected = viewModel.levelState?.firefliesCollected ?: 0
    val graphicMode by viewModel.graphicMode.collectAsState()
    val shakeAnim = remember { Animatable(0f) }
    val animateAngle by animateFloatAsState(
        targetValue = viewModel.getBirdRotation()
    )

    val spriteState = rememberSpriteState(
        totalFrames = 9,
        framesPerRow = 3
    )

    val spriteSpec = remember {
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = character.frameWidth,
                frameHeight = character.frameHeight,
                imageRes = character.sprite
            )
        )
    }
    val spriteImage: ImageBitmap = spriteSpec.imageBitmap
    val currentFrame by spriteState.currentFrame.collectAsState()
    val pipePairs = viewModel.pipePairs
    val pipeImage: ImageBitmap =
        BitmapFactory.decodeResource(context.resources, level.pipeImage).asImageBitmap()

    val pipeCapImage: ImageBitmap =
        BitmapFactory.decodeResource(context.resources, level.pipeCapImage).asImageBitmap()

    val pipeFlippedImage: ImageBitmap = flipBitmapVertically(pipeCapImage)

    val fireflyYoungImage: ImageBitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.dom_con).asImageBitmap()

    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.resetToastMessage()
        }
    }
    val invincibleAlpha by animateFloatAsState(
        targetValue = if (viewModel.isInvincible) 0.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 200),
            repeatMode = RepeatMode.Reverse
        )
    )
    BackHandler {
        viewModel.pauseGame()
        showPauseDialog = true
    }

    levelState?.windX?.let {
        if (it != 0f) {
            wind = true

        }
    }
    levelState?.windY?.let {
        if (it != 0f) {
            rain = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (viewModel.gameStatus == GameStatus.Started) {
                        viewModel.pauseGame()
                        showPauseDialog = true
                    }
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.initializeGame(characterName, levelNumber)
    }

    LaunchedEffect(gameStatus) {
        when (gameStatus) {
            GameStatus.Started -> spriteState.start()
            GameStatus.Over -> spriteState.stop()
            else -> {}
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            spriteState.stop()
            spriteState.cleanup()
        }
    }

    // Hide skill effect after delay
    LaunchedEffect(showSkillEffect) {
        if (showSkillEffect != null) {
            delay(1500)
            showSkillEffect = null
        }
    }

    val backgroundOffsetX = remember { Animatable(0f) }
    var imageWidth by remember { mutableIntStateOf(0) }

    LaunchedEffect(gameStatus) {
        if (gameStatus == GameStatus.Started) {
            while (true) {
                backgroundOffsetX.animateTo(
                    targetValue = -imageWidth.toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 4000,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        } else {
            backgroundOffsetX.snapTo(0f)
        }
    }


    LaunchedEffect(levelNumber, gameStatus) {
        if (levelNumber == 2 && gameStatus == GameStatus.Started) {
            while (isActive) {
                val startTime = System.currentTimeMillis()
                vibrateFor5s(context)

                while (System.currentTimeMillis() - startTime < 5000 && isActive) {
                    shakeAnim.animateTo(
                        targetValue = 5f,
                        animationSpec = tween(30, easing = LinearEasing)
                    )
                    shakeAnim.animateTo(
                        targetValue = -5f,
                        animationSpec = tween(30, easing = LinearEasing)
                    )
                    delay(10)
                }

                delay(10_000)
            }
        } else {
            shakeAnim.snapTo(0f)
        }
    }

    // Background Layer
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(shakeAnim.value.toInt(), 0) },
        contentAlignment = Alignment.BottomCenter
    ) {
        // Static background
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(level.image),
            contentDescription = "Background image",
            contentScale = ContentScale.Crop
        )

        Image(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged {
                    imageWidth = it.width
                }
                .offset {
                    IntOffset(
                        x = backgroundOffsetX.value.toInt(),
                        y = 0
                    )
                },
            painter = painterResource(level.movingImage),
            contentDescription = "Moving Background image",
            contentScale = ContentScale.FillHeight
        )
        Image(
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        x = backgroundOffsetX.value.toInt() + imageWidth,
                        y = 0
                    )
                },
            painter = painterResource(level.movingImage),
            contentDescription = "Moving Background image",
            contentScale = ContentScale.FillHeight
        )
    }

    if (levelState?.level?.levelNumber == 6 && gameStatus == GameStatus.Started) {
        BubbleEffect()
    }
    if (rain) {
        RainEffect()
    }

    if (wind) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            levelState?.windX?.let {
                WindEffect(
                    image = if (it > 0) downWindImage else crossWindImage,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .alpha(1f),
                    fromRight = it <= 0
                )
            }
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                val size = it.size
                if (screenHeight != size.height || screenWidth != size.width) {
                    screenHeight = size.height
                    screenWidth = size.width
                    viewModel.updateScreenSize(screenWidth, screenHeight)
                }
            }
            .pointerInput(gameStatus) {
                detectDragGestures(
                    onDragEnd = {
                        if (gameStatus == GameStatus.Started) {
                            viewModel.jump()
                        }
                    }
                ) { change, dragAmount ->
                    if (gameStatus == GameStatus.Started && character.skills.isNotEmpty()) {
                        val totalDrag = change.position - change.previousPosition
                        val distance = sqrt(totalDrag.x * totalDrag.x + totalDrag.y * totalDrag.y)

                        if (distance > 50f) {
                            when {
                                abs(totalDrag.x) > abs(totalDrag.y) && totalDrag.x > 0 &&
                                        character.skills.contains(Skill.DASH) -> {
                                    viewModel.useDashSkill()
                                    if (!viewModel.isDashOnCooldown()) {
                                        showSkillEffect = "DASH!"
                                    }
                                }

                                abs(totalDrag.y) > abs(totalDrag.x) && totalDrag.y < 0 &&
                                        character.skills.contains(Skill.HIGH_JUMP) -> {
                                    viewModel.useHighJumpSkill()
                                    if (!viewModel.isHighJumpOnCooldown()) {
                                        showSkillEffect = "HIGH JUMP!"
                                    }
                                }

                                abs(totalDrag.x) > abs(totalDrag.y) && totalDrag.x < 0 &&
                                        character.skills.contains(Skill.SLOW) -> {
                                    viewModel.useSlowSkill()
                                    if (!viewModel.isSlowOnCooldown()) {
                                        showSkillEffect = "SLOW!"
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (gameStatus == GameStatus.Started) {
                    viewModel.jump()
                }
            }
    ) {
        characterState?.let { character ->
            val halfWidth = character.width / 2
            val halfHeight = character.height / 2

            rotate(
                degrees = animateAngle,
                pivot = Offset(character.x, character.y)
            ) {
                drawSpriteView(
                    spriteState = spriteState,
                    spriteSpec = spriteSpec,
                    currentFrame = currentFrame,
                    image = spriteImage,
                    offset = IntOffset(
                        x = (character.x - halfWidth).toInt(),
                        y = (character.y - halfHeight).toInt()
                    ),
                )
            }
        }

        // Draw pipes
        pipePairs.forEach { pipePair ->
            val pipeWidth = viewModel.currentGameConfig.pipeWidth.toInt()

            // Top pipe body
            drawImage(
                image = pipeImage,
                dstOffset = IntOffset(
                    x = (pipePair.x - pipeWidth / 2).toInt(),
                    y = 0
                ),
                dstSize = IntSize(
                    width = pipeWidth,
                    height = (pipePair.topHeight - PIPE_CAP_HEIGHT).toInt()
                )
            )

            // Top pipe cap
            drawImage(
                image = pipeFlippedImage,
                dstOffset = IntOffset(
                    x = (pipePair.x - pipeWidth / 2).toInt(),
                    y = (pipePair.topHeight - PIPE_CAP_HEIGHT).toInt()
                ),
                dstSize = IntSize(
                    width = pipeWidth,
                    height = PIPE_CAP_HEIGHT.toInt()
                )
            )

            // Bottom pipe cap
            drawImage(
                image = pipeCapImage,
                dstOffset = IntOffset(
                    x = (pipePair.x - pipeWidth / 2).toInt(),
                    y = (pipePair.y + viewModel.currentGameConfig.pipeGapSize / 2).toInt()
                ),
                dstSize = IntSize(
                    width = pipeWidth,
                    height = PIPE_CAP_HEIGHT.toInt()
                )
            )

            // Bottom pipe body
            drawImage(
                image = pipeImage,
                dstOffset = IntOffset(
                    x = (pipePair.x - pipeWidth / 2).toInt(),
                    y = (pipePair.y + viewModel.currentGameConfig.pipeGapSize / 2 + PIPE_CAP_HEIGHT).toInt()
                ),
                dstSize = IntSize(
                    width = pipeWidth,
                    height = (pipePair.bottomHeight - PIPE_CAP_HEIGHT).toInt()
                )
            )
        }

        // Draw fireflies
        viewModel.fireflies.forEach { firefly ->
            drawImage(
                image = fireflyYoungImage,
                dstOffset = IntOffset(
                    x = (firefly.x - firefly.radius).toInt(),
                    y = (firefly.y - firefly.radius).toInt()
                ),
                dstSize = IntSize(
                    width = (firefly.radius * 5).toInt(),
                    height = (firefly.radius * 5).toInt()
                )
            )
        }

    }

    if (viewModel.isInvincible) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = stringResource(R.string.text_immortal),
                color = Color.Yellow,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                modifier = Modifier
                    .padding(top = 100.dp)
                    .alpha(invincibleAlpha)
            )
        }
    }

    showSkillEffect?.let { skillText ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = skillText,
                color = Color.Cyan,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                modifier = Modifier.alpha(0.8f)
            )
        }
    }

    FirefliesUI(
        collected = firefliesCollected,
        required = totalFirefly ?: 0,
    )

    // Score Banner
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Banner(
            text = "$currentScore"
        )
    }

    // Pause Button
    if (gameStatus == GameStatus.Started) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 24.dp),
            horizontalArrangement = Arrangement.End
        ) {
            CrashyBirdButton(
                onClick = {
                    viewModel.pauseGame()
                    showPauseDialog = true
                },
                paddingVertical = 12,
                paddingHorizontal = 12
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause button",
                    modifier = Modifier.size(24.dp),
                    tint = White
                )
            }
        }
    }

    //Skill and fps
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            character.skills.forEach { skill ->
                val iconRes = when (skill) {
                    Skill.DASH -> R.drawable.skill_dash
                    Skill.HIGH_JUMP -> R.drawable.skill_high_jump
                    Skill.SLOW -> R.drawable.skill_slow
                }
                val isOnCooldown = when (skill) {
                    Skill.DASH -> viewModel.isDashOnCooldown()
                    Skill.HIGH_JUMP -> viewModel.isHighJumpOnCooldown()
                    Skill.SLOW -> viewModel.isSlowOnCooldown()
                }
                SkillBadge(
                    icon = iconRes,
                    size = 64,
                    enabled = !isOnCooldown
                )
            }
        }

        Text(
            text = graphicMode.fps.toString(),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }

    GameStatusOverlay(
        gameStatus = gameStatus,
        currentScore = currentScore,
        viewModel = viewModel,
        navController = navController,
        backgroundOffsetX = backgroundOffsetX
    )

    if (showPauseDialog) {
        PauseDialog(
            viewModel = viewModel,
            onDismiss = {},
            onHomeClick = {
                navController.navigate(NavScreen.Home)
            },
            onResumeClick = {
                viewModel.resumeGame()
                showPauseDialog = false
            }
        )
    }
}

fun flipBitmapVertically(original: ImageBitmap): ImageBitmap {
    val matrix = android.graphics.Matrix().apply {
        preScale(1f, -1f)
    }
    val bmp = original.asAndroidBitmap()
    val flipped = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
    return flipped.asImageBitmap()
}
