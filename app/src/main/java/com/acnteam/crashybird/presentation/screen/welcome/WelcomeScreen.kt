package com.acnteam.crashybird.presentation.screen.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.acnteam.crashybird.R
import com.acnteam.crashybird.presentation.common.CrashyBirdButton
import com.acnteam.crashybird.presentation.common.rememberScreenHeight
import com.acnteam.crashybird.presentation.ui.theme.White
import kotlinx.coroutines.delay

@Preview
@Composable
fun WelcomeScreen(
    markDoneOnBoarding: () -> Unit = {},
) {
    var visibleCount by remember { mutableIntStateOf(1) }
    var isStartCounting by remember { mutableStateOf(false) }

    val story = listOf(
        Triple(
            R.string.story_1_title,
            R.drawable.story_1,
            1
        ),
        Triple(
            R.string.story_2_title,
            R.drawable.story_2,
            3
        ),
        Triple(
            R.string.story_3_title,
            R.drawable.story_3,
            4
        ),
    )

    val screenHeight = rememberScreenHeight()

    LaunchedEffect(story, isStartCounting) {
        if (isStartCounting) {
            for (i in 1..4) {
                visibleCount++
                delay(1000)
            }
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {
            if (!isStartCounting) {
                CrashyBirdButton(
                    onClick = { isStartCounting = true },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.btn_click_here),
                            color = White
                        )

                        Icon(
                            imageVector = Icons.Default.AdsClick,
                            contentDescription = "Click here button",
                            tint = White
                        )
                    }
                }
            }

            // Story
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Spacer(
                    modifier = Modifier.padding(8.dp)
                )
                story.forEach {
                    AnimatedVisibility(
                        visible = it.third < visibleCount,
                        enter = slideInHorizontally {
                            -500
                        }
                    ) {
                        StoryCard(
                            title = it.first,
                            image = it.second,
                            height = (screenHeight - 40.dp - innerPadding.calculateTopPadding()) / 3
                        )
                    }
                }
            }

            // Skip button
            AnimatedVisibility(
                visible = visibleCount > 2,
                enter = slideInHorizontally {
                    it + 500
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
            ) {
                CrashyBirdButton(
                    onClick = { markDoneOnBoarding() },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.btn_skip),
                            color = White
                        )

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Skip button",
                            tint = White
                        )
                    }
                }
            }
        }
    }
}