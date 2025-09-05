package com.acnteam.crashybird.presentation.screen.shop

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.Items
import com.acnteam.crashybird.presentation.common.CrashyBirdButton
import com.acnteam.crashybird.presentation.common.TextCard
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.GrayDark
import com.acnteam.crashybird.presentation.ui.theme.MyFontFamily
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent
import com.acnteam.crashybird.presentation.ui.theme.Pink40
import com.acnteam.crashybird.presentation.ui.theme.White

@Preview
@Composable
fun UnlockItemDialog(
    item: Items = Items.Shit,
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
                    text = item.name,
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
                    Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                        Image(
                            painterResource(item.image),
                            contentDescription = item.name,
                            modifier = Modifier
                                .height(item.itemHeight.dp)
                                .width(item.itemWidth.dp),
                            contentScale = ContentScale.FillBounds,
                            alignment = Alignment.Center
                        )
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
                            string = item.price.toString(),
                            bgColor = OrangeAccent,
                            color = White
                        )
                    }
                }

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