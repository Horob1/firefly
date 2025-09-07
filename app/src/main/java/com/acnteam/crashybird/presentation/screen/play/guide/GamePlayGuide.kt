package com.acnteam.crashybird.presentation.screen.play.guide

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import kotlinx.coroutines.delay
import com.acnteam.crashybird.R
@Composable
fun PlayGuideDialog(
    onDismiss: () -> Unit
) {
    val actions = listOf(
        "JUMP" to "Tap to jump",
        "DASH" to "Swipe right to dash",
        "HIGH JUMP" to "Swipe up for high jump",
        "SLOW" to "Swipe left to slow",
    )

    val skillImages = listOf(
        R.drawable.daotien,
        R.drawable.skill_dash,
        R.drawable.skill_high_jump,
        R.drawable.skill_slow
    )

    val fingerIcons = listOf(
        R.drawable.icon_finger,
        R.drawable.icon_finger_swipe_to_right,
        R.drawable.icon_finger_swipe_up,
        R.drawable.icon_finger_swipe_to_left
    )

    var currentStep by remember { mutableStateOf(0) }
    var showNext by remember { mutableStateOf(false) }
    val offsetX = remember { Animatable(-300f) }

    LaunchedEffect(currentStep) {
        offsetX.snapTo(-300f)
        offsetX.animateTo(0f, animationSpec = tween(1000))
        delay(1500)
        showNext = true
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .background(Color.DarkGray, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("PLAY GUIDE", color = Color.Yellow, fontSize = 28.sp)

            Spacer(Modifier.height(24.dp))

            Text(actions[currentStep].first, color = Color.White, fontSize = 24.sp)

            Spacer(Modifier.height(16.dp))

            if (currentStep > 0) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = skillImages[currentStep],
                        imageLoader = LocalContext.current.let {
                            coil.ImageLoader.Builder(it)
                                .components { add(SvgDecoder.Factory()) }
                                .build()
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(16.dp))
            }
            else{
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(1000)),
                    exit = fadeOut()
                ) {
                    Text(
                        text = stringResource(R.string.text_playjump),
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }            }
            Spacer(Modifier.height(16.dp))

            Text(
                text = actions[currentStep].second,
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.offset(x = offsetX.value.dp)
            )

            Spacer(Modifier.height(8.dp))

            Image(
                painter = painterResource(fingerIcons[currentStep]),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.height(32.dp))

            if (currentStep == 0) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(1200)),
                    exit = fadeOut()
                ) {
                    Text(
                        text = stringResource(R.string.text_playskill),
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            }

            if (showNext) {
                Button(onClick = {
                    if (currentStep < actions.size - 1) {
                        currentStep++
                        showNext = false
                    } else {
                        onDismiss()
                    }
                }) {
                    Text(if (currentStep == actions.size - 1) "Finish" else "Next")
                }
            }
        }
    }
}
