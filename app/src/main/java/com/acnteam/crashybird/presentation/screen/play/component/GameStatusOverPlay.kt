package com.acnteam.crashybird.presentation.screen.play.component

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.game.GameStatus
import com.acnteam.crashybird.presentation.common.CrashyBirdButton
import com.acnteam.crashybird.presentation.common.TripleOrbitLoadingAnimation
import com.acnteam.crashybird.presentation.navigation.NavScreen
import com.acnteam.crashybird.presentation.screen.play.PlayViewModel
import com.acnteam.crashybird.presentation.ui.theme.White
import com.acnteam.crashybird.utils.AdMobHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameStatusOverlay(
    gameStatus: GameStatus,
    currentScore: Int,
    viewModel: PlayViewModel,
    navController: NavController,
    backgroundOffsetX: Animatable<Float, AnimationVector1D>
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as Activity

    when (gameStatus) {
        GameStatus.Idle -> {
            LaunchedEffect(Unit) {
                AdMobHelper.loadRewardedAd(
                    activity,
                    "ca-app-pub-3940256099942544/5224354917"
                )
            }
            GameOverlay(
                title = null,
                score = null,
                buttons = listOf(
                    GameButton(stringResource(R.string.btn_start)) { viewModel.start() }
                )
            )
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                IconButton(
                    onClick = { viewModel.pauseGame() },
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_guide),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        GameStatus.Over -> {
            var isLoading by remember { mutableStateOf(false) }
            val buttons = mutableListOf<GameButton>().apply {
                add(GameButton(stringResource(R.string.btn_restart)) {
                    viewModel.restart()
                    scope.launch { backgroundOffsetX.snapTo(0f) }
                })

                add(GameButton(stringResource(R.string.btn_home)) {
                    navController.navigate(NavScreen.Home)
                })

                if (viewModel.canWatchReviveAd()) {
                    add(
                        GameButton(
                            imageRes = R.drawable.ads_img,
                            onClick = {
                                isLoading = true
                                AdMobHelper.showRewardedAd(
                                    activity,
                                    onReward = { rewardItem ->
                                        viewModel.pauseGame()
                                        viewModel.revive()
                                        scope.launch { backgroundOffsetX.snapTo(0f) }
                                    },
                                    onAdDismissed = {
                                        isLoading = false
                                        AdMobHelper.loadRewardedAd(
                                            activity,
                                            "ca-app-pub-3940256099942544/5224354917"
                                        )
                                    }
                                )
                            }
                        )
                    )

                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    TripleOrbitLoadingAnimation(modifier = Modifier.size(80.dp))
                }
            } else {
                GameOverlay(
                    title = stringResource(R.string.btn_gameover),
                    score = currentScore,
                    buttons = buttons
                )
            }
        }

        GameStatus.Reviving -> {
            CountdownOverlay()
        }

        else -> {}
    }
}

@Composable
private fun GameOverlay(
    title: String?,
    score: Int?,
    buttons: List<GameButton>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        title?.let {
            Text(
                text = it,
                color = Color.White,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
        }

        score?.let {
            Text(
                text = "${stringResource(R.string.coin)}: $it",
                color = Color.White,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        }

        if (title != null || score != null) {
            Spacer(modifier = Modifier.height(24.dp))
        }
        buttons.forEach { button ->
            if (button.imageRes != null && button.text == null) {
                IconButton(
                    onClick = button.onClick,
                    modifier = Modifier.size(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = button.imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                CrashyBirdButton(
                    modifier = Modifier.width(180.dp),
                    onClick = button.onClick
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        button.imageRes?.let {
                            Image(
                                painter = painterResource(id = it),
                                contentDescription = button.text,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        button.text?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleLarge,
                                color = White
                            )
                        }
                    }
                }
            }

            if (button != buttons.last()) {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CountdownOverlay() {
    var count by remember { mutableStateOf(3) }

    LaunchedEffect(Unit) {
        while (count > 1) {
            delay(1000)
            count--
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            style = TextStyle(
                color = Color.White,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

data class GameButton(
    val text: String? = null,
    val imageRes: Int? = null,
    val onClick: () -> Unit
)
