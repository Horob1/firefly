package com.acnteam.crashybird.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.acnteam.crashybird.presentation.ui.theme.GrayDark
import com.acnteam.crashybird.presentation.ui.theme.White

@Preview
@Composable
fun TextCard(
    string: String = "Click here",
    enabled: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    onClick: () -> Unit = {},
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = GrayDark.copy(
                alpha = 0.3f
            ),
            disabledContainerColor = GrayDark.copy(
                alpha = 0.3f
            ),
            disabledContentColor = White,
            contentColor = White,
        ),
        shape = MaterialTheme.shapes.extraLarge,
        enabled = enabled,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        onClick = {
            if (enabled) {
                onClick()
            }
        },
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                string, style = textStyle.copy(
                    color = White,
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}