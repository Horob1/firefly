package com.acnteam.crashybird.presentation.screen.play.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay

@Composable
fun WindEffect(
    image: ImageBitmap,
    modifier: Modifier = Modifier,
    onFinish: (() -> Unit)? = null,
    fromRight: Boolean = false
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(image, fromRight) {
        while (true) {
            // Bắt đầu thổi mạnh (10s full gió)
            progress.snapTo(1f)
            delay(10_000L)

            // Giảm dần trong 5s (từ 1f → 0f)
            progress.animateTo(
                0f,
                animationSpec = tween(durationMillis = 5_000, easing = LinearEasing)
            )

            // Nghỉ 10s (gió = 0)
            progress.snapTo(0f)
            delay(10_000L)

            onFinish?.invoke()
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val visibleW = w * progress.value

        if (fromRight) {
            clipRect(
                left = w - visibleW,
                top = 0f,
                right = w,
                bottom = h
            ) {
                drawImage(
                    image = image,
                    dstSize = IntSize(w.toInt(), h.toInt())
                )
            }
        } else {
            clipRect(
                left = 0f,
                top = 0f,
                right = visibleW,
                bottom = h
            ) {
                drawImage(
                    image = image,
                    dstSize = IntSize(w.toInt(), h.toInt())
                )
            }
        }
    }
}


@Composable
fun RainEffect(
    modifier: Modifier = Modifier,
    windX: Float = 2f
) {
    val raindrops = remember {
        List(100) {
            Offset(
                x = (0..1000).random().toFloat(),
                y = (0..2000).random().toFloat()
            )
        }.toMutableStateList()
    }

    LaunchedEffect(Unit) {
        while (true) {
            raindrops.forEachIndexed { index, drop ->
                val newY = drop.y + 15f
                val newX = drop.x + windX * 2f
                raindrops[index] = Offset(
                    x = (newX % 1080f),
                    y = if (newY > 2000f) 0f else newY
                )
            }
            delay(16)
        }
    }
    Canvas(modifier = modifier.fillMaxSize()) {
        raindrops.forEach { drop ->
            drawLine(
                color = Color.Cyan.copy(alpha = 0.7f),
                start = drop,
                end = drop + Offset(0f + windX, 20f),
                strokeWidth = 3f
            )
        }
    }
}
