package com.acnteam.crashybird.presentation.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.CharacterState
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.LevelState
import com.acnteam.crashybird.presentation.common.CrashyBirdButton
import com.acnteam.crashybird.presentation.common.sprite.component.drawSpriteView
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSheet
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSpec
import com.acnteam.crashybird.presentation.common.sprite.domain.rememberSpriteState
import com.acnteam.crashybird.presentation.ui.theme.Black
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent
import com.acnteam.crashybird.presentation.ui.theme.Pink80
import com.acnteam.crashybird.presentation.ui.theme.White
import com.canopas.lib.showcase.IntroShowcaseScope
import com.canopas.lib.showcase.component.ShowcaseStyle


@Preview
@Composable
fun IntroShowcaseScope.GamePanel(
    modifier: Modifier = Modifier,
    selectedMap: Level = Level.One,
    levels: List<LevelState> = emptyList(),
    selectedCharacter: Character = Character.Jack,
    characterList: List<CharacterState> = emptyList(),
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
            modifier = Modifier
                .width(160.dp)
                .introShowCaseTarget(
                    index = 6,
                    style = ShowcaseStyle.Default.copy(
                        backgroundColor = OrangeAccent, // specify color of background
                        backgroundAlpha = 0.98f, // specify transparency of background
                        targetCircleColor = White // specify color of target circle
                    ),
                    content = {
                        Text(
                            stringResource(R.string.desc_play),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Black
                            )
                        )
                    }
                ),
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
            selectedCharacter = selectedCharacter,
            characters = characterList,
            onLeftCharacterClick = onLeftCharacterClick,
            onRightCharacterClick = onRightCharacterClick,
            onLeftMapClick = onLeftMapClick,
            onRightMapClick = onRightMapClick,
            onUnlockClick = onUnlockClick
        )


        CrashyBirdButton(
            modifier = Modifier
                .width(240.dp)
                .introShowCaseTarget(
                    index = 3,
                    style = ShowcaseStyle.Default.copy(
                        backgroundColor = OrangeAccent, // specify color of background
                        backgroundAlpha = 0.98f, // specify transparency of background
                        targetCircleColor = White // specify color of target circle
                    ),
                    content = {
                        Text(
                            stringResource(R.string.desc_setting_btn),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Black
                            )
                        )
                    }
                ),
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
fun IntroShowcaseScope.GameController(
    selectedMap: Level = Level.One,
    levels: List<LevelState> = emptyList(),
    onLeftMapClick: () -> Unit = {},
    onRightMapClick: () -> Unit = {},
    selectedCharacter: Character = Character.Jack,
    characters: List<CharacterState> = emptyList(),
    onLeftCharacterClick: () -> Unit = {},
    onRightCharacterClick: () -> Unit = {},
    onUnlockClick: () -> Unit = {},
) {
    // định nghĩa kích thước của sprite và sheet(kích thước mỗi frame và ảnh nhân vật)
    val spriteSpec = remember(selectedCharacter) {
        SpriteSpec(
            screenWidth = 240f,
            default = SpriteSheet(
                frameWidth = selectedCharacter.frameWidth,
                frameHeight = selectedCharacter.frameHeight,
                imageRes = selectedCharacter.sprite
            )
        )
    }
    // khởi tạo state của sprite gồm tổng số frame và số frame mỗi hàng
    val spriteState = rememberSpriteState(
        totalFrames = 9,
        framesPerRow = 3
    )

    //Theo dõi frame hiện tại
    val currentFrame by spriteState.currentFrame.collectAsState()
    val spriteImage: ImageBitmap = spriteSpec.imageBitmap

    LaunchedEffect(selectedCharacter) {
        spriteState.start()
    }

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
                .height(140.dp)
                .width(220.dp),
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
                    onClick = {
                        onLeftCharacterClick()
                    },
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
                        .size(120.dp) // khung nhân vật
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        val spriteW = spriteSpec.spriteSheet.frameWidth.toFloat()
                        val spriteH = spriteSpec.spriteSheet.frameHeight.toFloat()

                        val offsetX = (size.width - spriteW) / 2
                        val offsetY = (size.height - spriteH) / 2

                        translate(offsetX, offsetY) {
                            // Hiển thị nhân vật
                            drawSpriteView(
                                spriteState = spriteState,
                                spriteSpec = spriteSpec,
                                currentFrame = currentFrame,
                                image = spriteImage
                            )
                        }
                    }
                    if (characters.isNotEmpty() && characters.find { it.character == selectedCharacter }?.isUnlocked == false)
                        IconButton(
                            onClick = { onUnlockClick() },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .introShowCaseTarget(
                                    index = 2,
                                    style = ShowcaseStyle.Default.copy(
                                        backgroundColor = OrangeAccent, // specify color of background
                                        backgroundAlpha = 0.98f, // specify transparency of background
                                        targetCircleColor = White // specify color of target circle
                                    ),
                                    content = {
                                        Text(
                                            stringResource(R.string.desc_unlock),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = Black
                                            )
                                        )
                                    }
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.lock),
                                contentDescription = "Lock",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                }


                IconButton(
                    onClick = {
                        onRightCharacterClick()
                    },
                    modifier = Modifier.introShowCaseTarget(
                        index = 0,
                        style = ShowcaseStyle.Default.copy(
                            backgroundColor = OrangeAccent, // specify color of background
                            backgroundAlpha = 0.98f, // specify transparency of background
                            targetCircleColor = White // specify color of target circle
                        ),
                        content = {
                            Text(
                                stringResource(R.string.txt_choose_character_button),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Black
                                )
                            )
                        }
                    )
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
            paddingHorizontal = 0,
            modifier = Modifier.introShowCaseTarget(
                index = 1,
                style = ShowcaseStyle.Default.copy(
                    backgroundColor = OrangeAccent, // specify color of background
                    backgroundAlpha = 0.98f, // specify transparency of background
                    targetCircleColor = White // specify color of target circle
                ),
                content = {
                    Text(
                        stringResource(R.string.txt_choose_map_button),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Black
                        )
                    )
                }
            )
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