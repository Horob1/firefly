package com.acnteam.crashybird.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent
import com.acnteam.crashybird.presentation.ui.theme.Pink40

@Preview
@Composable
fun CrashyBirdButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    paddingHorizontal: Int = 24,
    paddingVertical: Int = 12,
    content: @Composable () -> Unit = {
        Text(
            text = "Button",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    },
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        OrangeAccent,
                        BrownPrimary,
                    )
                )
            )
            .border(2.dp, Pink40, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = paddingHorizontal.dp, vertical = paddingVertical.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}


