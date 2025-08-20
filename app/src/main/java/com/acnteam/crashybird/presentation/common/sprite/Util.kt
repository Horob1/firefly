package com.acnteam.crashybird.presentation.common.sprite

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.appcompat.content.res.AppCompatResources

/**
 * Get current screen category based on screen size
 */
@Composable
fun getCurrentScreenCategory(): ScreenCategory {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    val smallestWidth = minOf(screenWidthDp, screenHeightDp)

    return when {
        smallestWidth < 360.dp -> ScreenCategory.Small
        smallestWidth < 600.dp -> ScreenCategory.Medium
        smallestWidth < 840.dp -> ScreenCategory.Large
        else -> ScreenCategory.ExtraLarge
    }
}

/**
 * Convert drawable resource to ImageBitmap
 */
@Composable
fun imageResource(@DrawableRes id: Int): ImageBitmap {
    val context = LocalContext.current
    val drawable = AppCompatResources.getDrawable(context, id)
    return drawable?.toBitmap()?.asImageBitmap()
        ?: throw IllegalArgumentException("Cannot load drawable resource $id")
}

/**
 * Convert drawable resource to ImageBitmap (non-composable version)
 */
fun Context.imageResource(@DrawableRes id: Int): ImageBitmap {
    val drawable = AppCompatResources.getDrawable(this, id)
    return drawable?.toBitmap()?.asImageBitmap()
        ?: throw IllegalArgumentException("Cannot load drawable resource $id")
}
