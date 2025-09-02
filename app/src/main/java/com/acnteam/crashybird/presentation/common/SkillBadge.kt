package com.acnteam.crashybird.presentation.common

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.acnteam.crashybird.R
import com.acnteam.crashybird.presentation.ui.theme.GrayDark
import com.acnteam.crashybird.presentation.ui.theme.White

@Preview
@Composable
fun SkillBadge(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int = R.drawable.skill_dash,
    size: Int = 360,
    enabled: Boolean = true,
) {

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size * 2 / 10).dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "Skill Base",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .clip(RoundedCornerShape((size * 2 / 10).dp)),
            alignment = Alignment.Center,
            contentScale = ContentScale.FillBounds
        )



        if (!enabled)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .background(
                        GrayDark.copy(0.8f)
                    )
                    .clip(RoundedCornerShape((size * 2 / 10).dp)),
                contentAlignment = Alignment.Center
            ) {
                NeonRotatingBorder(
                    sBox = size.dp,
                    corner = (size * 2 / 10).dp,
                    color = White
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.radar),
                        contentDescription = "Skill Base Lock",
                        modifier = Modifier
                            .fillMaxSize(3 / 10f)
                            .aspectRatio(1f),
                        alignment = Alignment.Center,
                        contentScale = ContentScale.FillBounds
                    )
                }

            }
        else Box(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .clip(RoundedCornerShape((size * 2 / 10).dp)).border(
                    width = 8.dp,
                    color = Color.Cyan,
                    shape = RoundedCornerShape((size * 2 / 10).dp)
                ),
        )
    }
}

@Composable
fun NeonRotatingBorder(
    modifier: Modifier = Modifier,
    sBox: Dp = 100.dp,
    corner: Dp = 20.dp,
    borderWidth: Dp = 6.dp,
    glowRadius: Dp = 12.dp,
    color: Color = Color.Cyan,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val infinite = rememberInfiniteTransition(label = "rotate")
    val angle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Box(
        modifier
            .size(sBox)
            .drawBehind {
                val strokePx = borderWidth.toPx()

                drawRoundRect(
                    color = color.copy(alpha = 0.3f),
                    style = Stroke(width = strokePx),
                    cornerRadius = CornerRadius(corner.toPx(), corner.toPx())
                )

                drawIntoCanvas { canvas ->
                    val paint = Paint()
                    val frameworkPaint = paint.asFrameworkPaint().apply {
                        isAntiAlias = true
                        this.color = color.toArgb()
                        style = android.graphics.Paint.Style.STROKE
                        strokeWidth = strokePx
                        strokeCap = android.graphics.Paint.Cap.ROUND
                        maskFilter = android.graphics.BlurMaskFilter(
                            glowRadius.toPx(),
                            android.graphics.BlurMaskFilter.Blur.NORMAL
                        )
                    }

                    val rect = android.graphics.RectF(
                        strokePx / 2f,
                        strokePx / 2f,
                        size.width - strokePx / 2f,
                        size.height - strokePx / 2f
                    )

                    canvas.nativeCanvas.drawArc(
                        rect,
                        angle,
                        60f,
                        false,
                        frameworkPaint
                    )

                    frameworkPaint.maskFilter = null
                }
            }
            .clip(RoundedCornerShape(corner)),
        contentAlignment = Alignment.Center,
        content = content
    )
}
