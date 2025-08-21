package com.acnteam.crashybird.presentation.screen.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.presentation.common.CrashyBirdButton
import com.acnteam.crashybird.presentation.common.TextCard
import com.acnteam.crashybird.presentation.common.sprite.component.drawSpriteView
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSheet
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSpec
import com.acnteam.crashybird.presentation.common.sprite.domain.rememberSpriteState
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.GrayDark
import com.acnteam.crashybird.presentation.ui.theme.MyFontFamily
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent
import com.acnteam.crashybird.presentation.ui.theme.Pink40
import com.acnteam.crashybird.presentation.ui.theme.White

@Preview
@Composable
fun UnlockDialog(
    character: Character = Character.Jack,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(260.dp)
                .clip(
                    shape = MaterialTheme.shapes.medium
                )
                .border(
                    width = 1.dp,
                    color = White,
                    shape = MaterialTheme.shapes.medium
                )
                .background(
                    GrayDark
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(
                        shape = MaterialTheme.shapes.medium
                    )
                    .border(
                        width = 1.dp,
                        color = Pink40,
                        shape = MaterialTheme.shapes.medium
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                OrangeAccent,
                                BrownPrimary,
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = MyFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = White
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sprite
                    val spriteSpec = remember(character) {
                        SpriteSpec(
                            screenWidth = 240f,
                            default = SpriteSheet(
                                frameWidth = character.frameWidth,
                                frameHeight = character.frameHeight,
                                imageRes = character.sprite
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

                    LaunchedEffect(character) {
                        spriteState.start()
                    }

                    Canvas(modifier = Modifier.size(120.dp)) {
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

                    // Giá:
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.txt_price),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = MyFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = White
                        )
                        TextCard(
                            string = character.coin.toString(),
                            bgColor = OrangeAccent,
                            color = White
                        )


                    }
                }

                Text(
                    text = stringResource(character.desc),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = MyFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = White
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CrashyBirdButton(
                        modifier = Modifier.width(100.dp),
                        onClick = onConfirm,
                        paddingVertical = 8,
                        paddingHorizontal = 8
                    ) {
                        Text(
                            text = stringResource(R.string.btn_confirm),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = MyFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = White
                        )
                    }
                    CrashyBirdButton(
                        modifier = Modifier.width(100.dp),
                        onClick = onDismiss,
                        paddingVertical = 8,
                        paddingHorizontal = 8
                    ) {
                        Text(
                            text = stringResource(R.string.btn_cancel),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = MyFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = White
                        )
                    }
                }

            }
        }
    }

}