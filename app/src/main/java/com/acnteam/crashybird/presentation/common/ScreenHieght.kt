package com.acnteam.crashybird.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun rememberScreenHeight(): Dp {
    val configuration = LocalConfiguration.current
    return remember {
        configuration.screenHeightDp.dp
    }
}