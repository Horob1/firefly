package com.acnteam.crashybird.presentation.screen.play.component

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.xr.compose.testing.toDp
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent
import com.acnteam.crashybird.presentation.ui.theme.Pink40
import com.acnteam.crashybird.presentation.ui.theme.White

@Composable
fun FirefliesUI(
    collected: Int,
    required: Int,
    @DrawableRes itemImage: Int,
    ) {
    if(collected<required){
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(all = 24.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            OrangeAccent,
                            BrownPrimary,
                        )
                    )
                )
                .border(2.dp, Pink40, RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = itemImage),
                contentDescription = "Firefly Icon",
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "$collected / $required",
                color = White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
    else {
        NextMapUnlockedBanner()
    }
}
@SuppressLint("ConfigurationScreenWidthHeight", "RestrictedApi")
@Composable
fun NextMapUnlockedBanner() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val textWidth = remember { mutableStateOf(0.dp) }
    val offsetX = remember { Animatable(screenWidth.value) }
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        repeat(3) {
            offsetX.snapTo(screenWidth.value)
            offsetX.animateTo(
                targetValue = -textWidth.value.value,
                animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
            )
        }
        visible = false
    }

    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .height(40.dp)
                .background(Brush.horizontalGradient(listOf(OrangeAccent, BrownPrimary))),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Next Map is Unlocked!",
                color = White,
                modifier = Modifier
                    .offset { IntOffset(offsetX.value.dp.roundToPx(), 0) }
                    .onGloballyPositioned { coordinates ->
                        textWidth.value = coordinates.size.width.toDp()
                    }
            )
        }
    }
}
