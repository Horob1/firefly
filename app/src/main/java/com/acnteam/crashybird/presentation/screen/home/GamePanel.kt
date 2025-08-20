package com.acnteam.crashybird.presentation.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.LevelState
import com.acnteam.crashybird.presentation.common.CrashyBirdButton
import com.acnteam.crashybird.presentation.ui.theme.Pink80
import com.acnteam.crashybird.presentation.ui.theme.White

@Preview
@Composable
fun GamePanel(
    modifier: Modifier = Modifier,
    selectedMap: Level = Level.One,
    levels: List<LevelState> = emptyList(),
    onPlayClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onLeftCharacterClick: () -> Unit = {},
    onRightCharacterClick: () -> Unit = {},
    onLeftMapClick: () -> Unit = {},
    onRightMapClick: () -> Unit = {},
    onUnlockClick: () -> Unit = {},
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        CrashyBirdButton(
            modifier = Modifier.width(160.dp),
            onClick = onPlayClick,
        ) {
            Image(
                painterResource(R.drawable.play),
                contentDescription = "Play button",
                modifier = Modifier.size(40.dp),
            )
        }

        GameController(
            selectedMap = selectedMap,
            levels = levels,
            onLeftMapClick = onLeftMapClick,
            onRightMapClick = onRightMapClick,
        )


        CrashyBirdButton(
            modifier = Modifier.width(240.dp),
            onClick = onSettingClick,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    stringResource(R.string.btn_setting),
                    style = MaterialTheme.typography.titleLarge,
                    color = White
                )

                Icon(Icons.Default.Settings, contentDescription = "Setting button", tint = White)
            }
        }
    }
}

@Preview
@Composable
fun GameController(
    selectedMap: Level = Level.One,
    levels: List<LevelState> = emptyList(),
    onLeftMapClick: () -> Unit = {},
    onRightMapClick: () -> Unit = {},
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CrashyBirdButton(
            onClick = {
                if (levels.isNotEmpty() && levels.first().level.levelNumber != selectedMap.levelNumber) onLeftMapClick()
            },
            paddingVertical = 0,
            paddingHorizontal = 0
        ) {
            Icon(
                Icons.AutoMirrored.Default.ArrowLeft,
                contentDescription = "Left character button",
                modifier = Modifier.size(40.dp),
                tint = White
            )
        }

        Box(
            modifier = Modifier
                .height(160.dp)
                .width(240.dp),
            contentAlignment = Alignment.Center
        ) {

            AnimatedContent(
                targetState = selectedMap,
                label = "Level",
            ) {
                Image(
                    painter = painterResource(id = it.image),
                    contentDescription = "Map",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .border(
                            width = 2.dp,
                            color = Pink80,
                            shape = MaterialTheme.shapes.medium
                        )

                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /*TODO*/ },
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowLeft,
                        contentDescription = "Unlock button",
                        modifier = Modifier.size(40.dp),
                        tint = White
                    )
                }

                Box(
                    modifier = Modifier
                        .height(120.dp)
                        .width(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Character",
                    )

                    IconButton(
                        onClick = { /*TODO*/ },
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.lock),
                            contentDescription = "Lock",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                }

                IconButton(
                    onClick = { /*TODO*/ },
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = "Unlock button",
                        modifier = Modifier.size(40.dp),
                        tint = White
                    )
                }
            }
        }

        CrashyBirdButton(
            onClick = {
                if (levels.isNotEmpty() && levels.last().level.levelNumber != selectedMap.levelNumber) onRightMapClick()
            },
            paddingVertical = 0,
            paddingHorizontal = 0
        ) {
            Icon(
                Icons.AutoMirrored.Default.ArrowRight,
                contentDescription = "Left map button",
                modifier = Modifier.size(40.dp),
                tint = White
            )
        }
    }
}