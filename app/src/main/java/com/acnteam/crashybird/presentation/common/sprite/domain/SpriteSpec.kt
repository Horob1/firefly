package com.acnteam.crashybird.presentation.common.sprite.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import com.acnteam.crashybird.presentation.common.sprite.util.parseCategory
import androidx.compose.ui.platform.LocalResources

data class SpriteSpec(
    val screenWidth: Float,
    val default: SpriteSheet,
    val small: SpriteSheet? = null,
    val normal: SpriteSheet? = null,
    val large: SpriteSheet? = null,
    val tablet: SpriteSheet? = null
) {
    val screenCategory = screenWidth.parseCategory()

    val spriteSheet: SpriteSheet
        get() =
            if (screenCategory == ScreenCategory.Small && small != null) small
            else if (screenCategory == ScreenCategory.Normal && normal != null) normal
            else if (screenCategory == ScreenCategory.Large && large != null) large
            else if (screenCategory == ScreenCategory.Tablet && tablet != null) tablet
            else default

    @get:Composable
    val imageBitmap: ImageBitmap
        get() = ImageBitmap.imageResource(LocalResources.current, spriteSheet.imageRes)
}