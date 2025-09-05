package com.acnteam.crashybird.presentation.screen.shop

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.CharacterState
import com.acnteam.crashybird.domain.model.ItemState
import com.acnteam.crashybird.presentation.common.NotificationAlertDialog
import com.acnteam.crashybird.presentation.common.TextCard
import com.acnteam.crashybird.presentation.common.TripleOrbitLoadingAnimation
import com.acnteam.crashybird.presentation.common.UiState
import com.acnteam.crashybird.presentation.common.sprite.component.drawSpriteView
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSheet
import com.acnteam.crashybird.presentation.common.sprite.domain.SpriteSpec
import com.acnteam.crashybird.presentation.common.sprite.domain.rememberSpriteState
import com.acnteam.crashybird.presentation.screen.home.UnlockDialog
import com.acnteam.crashybird.utils.center

@Composable
fun ShopScreen(
    viewModel: ShopViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showUnlockSuccess = remember {
        mutableStateOf(false)
    }
    val showUnlockFail = remember {
        mutableStateOf(false)
    }
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

            if (state is UiState.Success)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 180.dp, bottom = 80.dp)
                        .padding(horizontal = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val stateData = (state as UiState.Success<ShopUiState>).data
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

                        val showUnlockChar = remember { mutableStateOf(false) }
                        var temp by remember { mutableStateOf((state as UiState.Success<ShopUiState>).data.currentCharacter) }

                        if (showUnlockChar.value) {
                            UnlockDialog(
                                character = temp,
                                onDismiss = {
                                    showUnlockChar.value = false
                                },
                                onConfirm = {
                                    showUnlockChar.value = false
                                    viewModel.unlockCharacter(temp, onSuccess = {
                                        showUnlockSuccess.value = true
                                    }, onFail = {
                                        showUnlockFail.value = true
                                    })
                                },
                            )
                        }


                        val oddChar = stateData.characters.filterIndexed { index, _ ->
                            index % 2 == 1
                        }

                        val evenChar = stateData.characters.filterIndexed { index, _ ->
                            index % 2 == 0
                        }

                        LazyRow(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(evenChar.size, key = { index ->
                                evenChar[index].character.name
                            }
                            ) { index ->
                                CharacterCard(
                                    characterState = evenChar[index],
                                    unlock = {
                                        temp = evenChar[index].character
                                        showUnlockChar.value = true
                                    },
                                    choose = {
                                        if (evenChar[index].character.name != (state as UiState.Success<ShopUiState>).data.currentCharacter.name)
                                            viewModel.setCurrentCharacter(evenChar[index].character)
                                    },
                                    isChoosing = evenChar[index].character.name == (state as UiState.Success<ShopUiState>).data.currentCharacter.name
                                )
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
                            items(oddChar.size, key = { index ->
                                oddChar[index].character.name
                            }) { index ->
                                CharacterCard(
                                    characterState = oddChar[index],
                                    unlock = {
                                        temp = oddChar[index].character
                                        showUnlockChar.value = true
                                    },
                                    choose = {
                                        if (oddChar[index].character.name != (state as UiState.Success<ShopUiState>).data.currentCharacter.name)
                                            viewModel.setCurrentCharacter(oddChar[index].character)
                                    },
                                    isChoosing = oddChar[index].character.name == (state as UiState.Success<ShopUiState>).data.currentCharacter.name
                                )
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

                        val showUnlockItem = remember { mutableStateOf(false) }
                        var tempItem by remember { mutableStateOf((state as UiState.Success<ShopUiState>).data.currentItem) }

                        if (showUnlockItem.value) {
                            UnlockItemDialog(
                                item = tempItem,
                                onDismiss = {
                                    showUnlockItem.value = false
                                },
                                onConfirm = {
                                    showUnlockItem.value = false
                                    viewModel.unlockItem(tempItem, onSuccess = {
                                        showUnlockSuccess.value = true
                                    }, onFail = {
                                        showUnlockFail.value = true
                                    })
                                },
                            )
                        }

                        val oddItem = stateData.items.filterIndexed { index, _ ->
                            index % 2 == 1
                        }

                        val evenItem = stateData.items.filterIndexed { index, _ ->
                            index % 2 == 0
                        }

                        LazyRow(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(evenItem.size, key = { index ->
                                evenItem[index].item.name
                            }) { index ->
                                ItemCard(
                                    itemState = evenItem[index],
                                    unlock = {
                                        tempItem = evenItem[index].item
                                        showUnlockItem.value = true
                                    },
                                    choose = {
                                        if (evenItem[index].item.name != (state as UiState.Success<ShopUiState>).data.currentItem.name)
                                            viewModel.setCurrentItem(evenItem[index].item)
                                    },
                                    isChoosing = evenItem[index].item.name == (state as UiState.Success<ShopUiState>).data.currentItem.name
                                )
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
                            items(oddItem.size, key = { index ->
                                oddItem[index].item.name
                            }) { index ->
                                ItemCard(
                                    itemState = oddItem[index],
                                    unlock = {
                                        tempItem = oddItem[index].item
                                        showUnlockItem.value = true
                                    },
                                    choose = {
                                        if (oddItem[index].item.name != (state as UiState.Success<ShopUiState>).data.currentItem.name)
                                            viewModel.setCurrentItem(oddItem[index].item)
                                    },
                                    isChoosing = oddItem[index].item.name == (state as UiState.Success<ShopUiState>).data.currentItem.name
                                )
                            }
                        }

                    }
                } else {
                TripleOrbitLoadingAnimation(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(140.dp)
                )
            }

            if (state is UiState.Success)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp),
                ) {
                    TextCard(
                        string = "${stringResource(R.string.txt_coin)}: ${
                            (state as UiState.Success<ShopUiState>).data.coin.toString().center(8)
                        }",
                    )
                }
            if (showUnlockSuccess.value) {
                NotificationAlertDialog(
                    onDismiss = {
                        showUnlockSuccess.value = false
                    },
                    R.string.alert_unlock_success,
                )
            }

            if (showUnlockFail.value) {
                NotificationAlertDialog(
                    onDismiss = {
                        showUnlockFail.value = false
                    },
                    R.string.alert_is_not_enough_coin,
                )
            }
        }
    }
}

@Composable
fun ItemCard(
    modifier: Modifier = Modifier,
    itemState: ItemState,
    unlock: () -> Unit,
    choose: () -> Unit,
    isChoosing: Boolean,
) {
    Box(
        modifier = modifier
            .fillMaxHeight(0.8f)
            .aspectRatio(1f)
            .zIndex(1f)
            .clickable(
                enabled = true,
                onClick = {
                    if (itemState.isUnlocked)
                        choose()
                    else
                        unlock()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = Color(0xFFFEC993),
                    shape = MaterialTheme.shapes.medium
                )
                .border(
                    width = 4.dp,
                    color = if (isChoosing) Color(0xFF15B203) else Color.White,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Image(
                painterResource(itemState.item.image),
                contentDescription = itemState.item.name,
                modifier = Modifier
                    .height(itemState.item.itemHeight.dp)
                    .width(itemState.item.itemWidth.dp),
                contentScale = ContentScale.Inside,
                alignment = Alignment.Center
            )
        }


        if (!itemState.isUnlocked)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer {
                        translationY = size.height / 2f
                    }
                    .zIndex(10f)
                    .clickable(
                        enabled = true,
                        onClick = {
                            if (itemState.isUnlocked)
                                choose()
                            else
                                unlock()
                        }
                    ),
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
                        itemState.item.price.toString().center(6),
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
    }
}

@Composable
fun CharacterCard(
    modifier: Modifier = Modifier,
    characterState: CharacterState = CharacterState(Character.Jack, false),
    isChoosing: Boolean = false,
    unlock: () -> Unit = {},
    choose: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxHeight(0.8f)
            .aspectRatio(1f)
            .zIndex(1f)
            .clickable(
                enabled = true,
                onClick = {
                    if (characterState.isUnlocked)
                        choose()
                    else
                        unlock()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = Color(0xFFFEC993),
                    shape = MaterialTheme.shapes.medium
                )
                .border(
                    width = 4.dp,
                    color = if (isChoosing) Color(0xFF15B203) else Color.White,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            // Character
            // ...
            val spriteSpec = remember(characterState) {
                SpriteSpec(
                    screenWidth = 240f,
                    default = SpriteSheet(
                        frameWidth = characterState.character.frameWidth,
                        frameHeight = characterState.character.frameHeight,
                        imageRes = characterState.character.sprite
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

            LaunchedEffect(characterState) {
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
        if (!characterState.isUnlocked)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer {
                        translationY = size.height / 2f
                    }
                    .zIndex(10f)
                    .clickable(
                        enabled = true,
                        onClick = {
                            if (characterState.isUnlocked)
                                choose()
                            else
                                unlock()
                        }
                    ),
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
                        characterState.character.coin.toString().center(6),
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
    }
}