package com.acnteam.crashybird.presentation.screen.play

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.acnteam.crashybird.R
import com.acnteam.crashybird.presentation.common.sprite.component.drawSpriteView
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSheet
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSpec
import com.acnteam.crashybird.presentation.common.sprite.domain.rememberSpriteState

@Composable
fun PlayScreen() {
    var screenHeight by remember { mutableStateOf(0) }
    var screenWidth by remember { mutableStateOf(0) }

    // Sprite state 9 frame, 3 frame per row
    val spriteState = rememberSpriteState(
        totalFrames = 9,
        framesPerRow = 3
    )

    val spriteSpec = remember {
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = 160,
                frameHeight = 103,
                imageRes = R.drawable.jack
            )
        )
    }

    val spriteImage: ImageBitmap = spriteSpec.imageBitmap
    val currentFrame by spriteState.currentFrame.collectAsState()

    // Animation: fly up-down infinitely
    val transition = rememberInfiniteTransition()
    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        spriteState.start()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan) // Background màu xanh
            .onSizeChanged { size ->
                screenWidth = size.width
                screenHeight = size.height
            },
        contentAlignment = Alignment.TopCenter
    ) {
        // Sprite
        Canvas(
            modifier = Modifier
                .width(with(LocalDensity.current) { spriteSpec.spriteSheet.frameWidth.dp })
                .height(with(LocalDensity.current) { spriteSpec.spriteSheet.frameHeight.dp })
                .offset { IntOffset(x = (screenWidth / 2 - spriteSpec.spriteSheet.frameWidth / 2), y = offsetY.toInt()) }
        ) {
            drawSpriteView(
                spriteState = spriteState,
                spriteSpec = spriteSpec,
                currentFrame = currentFrame,
                image = spriteImage
            )
        }
    }

}