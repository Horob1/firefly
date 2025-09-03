package com.acnteam.crashybird.presentation.screen.setting

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.GraphicMode
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent
import com.acnteam.crashybird.presentation.ui.theme.Pink40
import com.acnteam.crashybird.presentation.ui.theme.White


@RequiresApi(Build.VERSION_CODES.R)
@Preview(showBackground = true)
@Composable
fun GraphicPicker(
    modifier: Modifier = Modifier,
    currentGraphicMode: GraphicMode = GraphicMode.LOW,
    onGraphicModeChange: (GraphicMode) -> Unit = {},
) {
    val context = LocalContext.current

    val graphicModeList = remember { GraphicMode.getAllGraphicMode() }

    Column(
        modifier = modifier
            .background(
                color = BrownPrimary.copy(
                    alpha = 0.3f
                ),
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.btn_choose_fps),
            modifier = modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                color = White,
                fontStyle = FontStyle.Italic,
            )
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(2.dp, Pink40, RoundedCornerShape(10.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            graphicModeList.forEachIndexed { index, mode ->
                val isSelected = mode == currentGraphicMode

                val shape = when (index) {
                    0 -> RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                    graphicModeList.lastIndex -> RoundedCornerShape(
                        topEnd = 10.dp,
                        bottomEnd = 10.dp
                    )

                    else -> RoundedCornerShape(0.dp)
                }

                var childModifier = Modifier
                    .weight(1f)
                    .clip(shape)

                childModifier = if (isSelected)
                    childModifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                OrangeAccent,
                                BrownPrimary,
                            )
                        )
                    )
                else
                    childModifier.background(
                        color = Color.Transparent
                    )

                Box(
                    modifier = childModifier
                        .clickable {
                            val display = context.display
                            val refreshRate = display.refreshRate
                            if (refreshRate >= mode.fps.toFloat()) {
                                onGraphicModeChange(mode)
                            } else {
                                Toast.makeText(context, context.getText(R.string.alert_your_device_not_support_this_fps), Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = mode.name),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


