package com.acnteam.crashybird.presentation.screen.play.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.sin

// dữ liệu bong bóng
private data class BubbleState(
    var xRatio: Float,     // vị trí X theo tỉ lệ màn hình
    var radius: Float,     // bán kính bong bóng
    var speed: Float,      // tốc độ nổi
    var phase: Float,      // để tạo hiệu ứng lắc lư
    var offsetProgress: Float // điểm bắt đầu (0..1) để bong bóng xuất hiện đều
)

@Preview(showBackground = true)
@Composable
fun BubbleEffect(
    modifier: Modifier = Modifier,
    bubbleCount: Int = 20
) {
    val transition = rememberInfiniteTransition()
    // chạy vòng lặp thời gian
    val timeAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing) // chậm hơn cho rõ
        )
    )

    // tạo danh sách bong bóng
    val bubbles = remember {
        List(bubbleCount) { i ->
            BubbleState(
                xRatio = Math.random().toFloat(),
                radius = (20..50).random().toFloat(),     // bong bóng to hơn
                speed = 0.5f, // tốc độ đa dạng
                phase = Math.random().toFloat() * 6.28f,
                offsetProgress = i.toFloat() / bubbleCount // chia đều thời gian xuất hiện
            )
        }
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            bubbles.forEach { b ->
                // thêm offset để bong bóng không đồngit g bộ
                val progress = (timeAnim + b.offsetProgress) % 1f
                val y = h * (1f - progress)
                // vị trí X dao động nhẹ trái/phải
                val x = b.xRatio * w + 25f * sin(progress * 6f + b.phase)

                // độ trong suốt giảm ít hơn để rõ ràng hơn
                val alpha = ((y / h) * 1.2f).coerceIn(0f, 1f)

                // vẽ bong bóng chính
                drawCircle(
                    color = Color.White.copy(alpha = 0.25f * alpha),
                    radius = b.radius,
                    center = Offset(x, y),
                    style = Fill
                )

                // highlight sáng hơn
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f * alpha),
                    radius = b.radius * 0.4f,
                    center = Offset(x - b.radius * 0.3f, y - b.radius * 0.3f),
                    style = Fill
                )
            }
        }
    }
}
