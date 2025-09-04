package com.acnteam.crashybird.presentation.screen.shop

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.presentation.common.TextCard
import com.acnteam.crashybird.presentation.common.sprite.component.drawSpriteView
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSheet
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSpec
import com.acnteam.crashybird.presentation.common.sprite.domain.rememberSpriteState
import com.acnteam.crashybird.utils.center

@Preview(showBackground = true)
@Composable
fun ShopScreen(
) {
    Scaffold {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.shop_bg),
                contentDescription = "background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.Center
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 180.dp, bottom = 80.dp)
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        "Characters",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold

                        )
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    LazyRow(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(10) { index ->
                            CharacterCard()
                        }
                    }

                    LazyRow(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(10) {
                            CharacterCard()
                        }
                    }

                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Items",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold

                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
            ) {


                TextCard(
                    string = "${stringResource(R.string.txt_coin)}: ${
                        "1000".center(8)
                    }",
                )
            }
        }
    }
}

@Composable
fun CharacterCard(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight(0.8f)
            .aspectRatio(1f)
            .zIndex(1f),
        contentAlignment = Alignment.Center
    ) {
        // Container có border + background
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = Color(0xFFFEC993),
                    shape = MaterialTheme.shapes.medium
                )
                .border(
                    width = 4.dp,
                    color = Color.White,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            // Character
            // ...
            val spriteSpec = remember(Character.Jack) {
                SpriteSpec(
                    screenWidth = 240f,
                    default = SpriteSheet(
                        frameWidth = Character.Jack.frameWidth,
                        frameHeight = Character.Jack.frameHeight,
                        imageRes = Character.Jack.sprite
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

            LaunchedEffect(Character.Jack) {
                spriteState.start()
            }

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

        }


        // Price -> đặt sau border nên nổi lên
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    translationY = size.height / 2f
                }
                .zIndex(10f),
        ) {
            Card(
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(
                    width = 2.dp,
                    color = Color(
                        0xFFA4C7A7
                    )
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF15B203)
                )

            ) {
                Text(
                    "10000".center(6),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }

}