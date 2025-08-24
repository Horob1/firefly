package com.acnteam.crashybird.presentation.screen.play

import PauseDialog
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.acnteam.crashybird.presentation.common.sprite.component.drawSpriteView
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSheet
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSpec
import com.acnteam.crashybird.presentation.common.sprite.domain.rememberSpriteState
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.game.GameStatus
import com.acnteam.crashybird.presentation.navigation.NavScreen
import com.acnteam.crashybird.presentation.screen.home.Banner
import kotlinx.coroutines.launch

const val PIPE_CAP_HEIGHT = 50f

@SuppressLint("LocalContextResourcesRead")
@Composable
fun PlayScreen(
    levelNumber: Int,
    characterName: String,
    viewModel: PlayViewModel,
    navController: NavController
) {
    var screenHeight by remember { mutableStateOf(0) }
    var screenWidth by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val character = Character.getByName(characterName)
    val level = Level.getByLevelNumber(levelNumber)

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

    val characterState = viewModel.characterState
    val levelState = viewModel.levelState
    val gameStatus = viewModel.gameStatus
    val currentScore = viewModel.currentScore
    val bestScore = viewModel.bestScore
    val pipePairs = viewModel.pipePairs
    val pipeImage: ImageBitmap =
        BitmapFactory.decodeResource(context.resources, level.pipeImage).asImageBitmap()

    val pipeCapImage: ImageBitmap =
        BitmapFactory.decodeResource(context.resources, level.pipeCapImage).asImageBitmap()

    val animateAngle by animateFloatAsState(
        targetValue = viewModel.getBirdRotation()
    )

    var showPauseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.initializeGame(characterName, levelNumber)
        spriteState.start()
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

    val scope = rememberCoroutineScope()
    val backgroundOffsetX = remember { Animatable(0f) }
    var imageWidth by remember { mutableStateOf(0) }

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

    // Background Layer
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Static background
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(level.image),
            contentDescription = "Background image",
            contentScale = ContentScale.Crop
        )

        // Moving background (parallax effect)
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

    // Game Canvas
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
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (gameStatus == GameStatus.Started) {
                    viewModel.jump()
                }
            }
    ) {
        // Draw character bằng sprite
        characterState?.let { character ->
            rotate(
                degrees = animateAngle,
                pivot = Offset(
                    x = character.x - character.radius,
                    y = character.y - character.radius
                )
            ) {
                drawSpriteView(
                    spriteState = spriteState,
                    spriteSpec = spriteSpec,
                    currentFrame = currentFrame,
                    image = spriteImage,
                    offset = IntOffset(
                        x = (character.x - character.radius).toInt(),
                        y = (character.y - character.radius).toInt()
                    )
                )
            }
        }

        // Draw pipes bằng image
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
                image = pipeCapImage,
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
    }


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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 24.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = {
                showPauseDialog = true
            }
        ) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }

    if (gameStatus == GameStatus.Idle) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Button(
                modifier = Modifier.height(54.dp),
                shape = RoundedCornerShape(size = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    viewModel.start()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "START",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            }
        }
    }

    if (gameStatus == GameStatus.Over) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.5f)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Game Over!",
                color = Color.White,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
            Text(
                text = "SCORE: $currentScore",
                color = Color.White,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                modifier = Modifier.height(54.dp),
                shape = RoundedCornerShape(size = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    viewModel.restart()
                    scope.launch {
                        backgroundOffsetX.snapTo(0f)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RESTART",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            }
        }
    }
    if (showPauseDialog) {
        PauseDialog(
            onDismiss = {},
            onHomeClick = {
                navController.navigate(
                    NavScreen.Home
                )
            },
            onResumeClick = {
                showPauseDialog = false
            }
        )
    }
}
