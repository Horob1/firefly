package com.acnteam.crashybird.presentation.common.sprite.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun getScreenWidth(): Dp {
    return LocalConfiguration.current.screenWidthDp.dp
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun getScreenHeight(): Dp {
    return LocalConfiguration.current.screenHeightDp.dp
}