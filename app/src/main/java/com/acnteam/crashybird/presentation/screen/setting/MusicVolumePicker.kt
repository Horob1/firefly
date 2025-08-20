package com.acnteam.crashybird.presentation.screen.setting

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.acnteam.crashybird.R
import com.acnteam.crashybird.presentation.common.CrashyBirdButton
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent
import com.acnteam.crashybird.presentation.ui.theme.Pink80
import com.acnteam.crashybird.presentation.ui.theme.White

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicVolumePicker(
    modifier: Modifier = Modifier,
    @StringRes title: Int = R.string.txt_background_music,
    sliderValue: Float = 0.5f,
    onValueChange: (Float) -> Unit = {},
    isEnabled: Boolean = true,
    toggleEnabled: () -> Unit = {},
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = BrownPrimary.copy(
                alpha = 0.3f
            )
        ),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(title),
                modifier = modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = White,
                    fontStyle = FontStyle.Italic,
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(8.dp)
            ) {
                CrashyBirdButton(
                    onClick = { toggleEnabled() },
                    paddingVertical = 0,
                    paddingHorizontal = 0
                ) {
                    Icon(
                        painter = painterResource(if (isEnabled) R.drawable.volume_on else R.drawable.volume_off),
                        contentDescription = "Left map button",
                        modifier = Modifier.size(36.dp),
                        tint = White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Slider(
                    value = sliderValue,
                    onValueChange = { onValueChange(it) },
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp),
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.Transparent, // ta tự vẽ
                        inactiveTrackColor = Color.Transparent
                    ),
                    thumb = {
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(
                                    2.dp,
                                    White,
                                    shape = RoundedCornerShape(6.dp)

                                )
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            OrangeAccent,
                                            BrownPrimary,
                                        )
                                    )
                                )
                        )
                    },
                    track = { sliderPositions ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(
                                    2.dp,
                                    Pink80,
                                    shape = RoundedCornerShape(6.dp)

                                )
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            BrownPrimary,
                                            OrangeAccent
                                        )
                                    )
                                )
                        )
                    }
                )
            }
        }
    }
}