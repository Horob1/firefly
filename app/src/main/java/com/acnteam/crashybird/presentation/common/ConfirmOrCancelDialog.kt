package com.acnteam.crashybird.presentation.common

import androidx.annotation.StringRes
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.acnteam.crashybird.R
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.GrayDark
import com.acnteam.crashybird.presentation.ui.theme.MyFontFamily
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent
import com.acnteam.crashybird.presentation.ui.theme.Pink40
import com.acnteam.crashybird.presentation.ui.theme.White

@Preview
@Composable
fun ConfirmOrCancelDialog(
    @StringRes title: Int = R.string.txt_notification,
    @StringRes message: Int = R.string.alert_is_not_enough_coin,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(360.dp)
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
                    text = stringResource(title),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = MyFontFamily,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    color = White
                )


            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(message),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = MyFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = White
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()

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

                    Spacer(modifier = Modifier.width(8.dp))

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