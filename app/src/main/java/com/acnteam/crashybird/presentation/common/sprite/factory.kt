package com.acnteam.crashybird.presentation.common.sprite

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable

/**
 * Create SpriteSpec from a single drawable resource (Composable version)
 */
@Composable
fun spriteSpecFromResource(
    @DrawableRes drawableRes: Int,
    frameWidth: Int,
    frameHeight: Int,
    totalFrames: Int,
    framesPerRow: Int,
    screenCategory: ScreenCategory = ScreenCategory.Medium
): SpriteSpec {
    val spriteSheet = SpriteSheet(
        frameWidth = frameWidth,
        frameHeight = frameHeight,
        totalFrames = totalFrames,
        framesPerRow = framesPerRow,
        screenCategory = screenCategory
    )

    val imageBitmap = imageResource(drawableRes)

    return SpriteSpec(spriteSheet, imageBitmap)
}

/**
 * Create SpriteSpec from a single drawable resource (non-Composable version)
 */
fun spriteSpecFromResource(
    context: Context,
    @DrawableRes drawableRes: Int,
    frameWidth: Int,
    frameHeight: Int,
    totalFrames: Int,
    framesPerRow: Int,
    screenCategory: ScreenCategory = ScreenCategory.Medium
): SpriteSpec {
    val spriteSheet = SpriteSheet(
        frameWidth = frameWidth,
        frameHeight = frameHeight,
        totalFrames = totalFrames,
        framesPerRow = framesPerRow,
        screenCategory = screenCategory
    )

    val imageBitmap = context.imageResource(drawableRes)

    return SpriteSpec(spriteSheet, imageBitmap)
}

/**
 * Create SpriteSpecBuilder for multiple screen categories
 */
fun spriteSpec(): SpriteSpecBuilder {
    return SpriteSpecBuilder()
}

