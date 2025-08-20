package com.acnteam.crashybird.presentation.common.sprite

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Screen categories for responsive sprite sheets
 */
enum class ScreenCategory {
    Small,      // Phone portrait
    Medium,     // Phone landscape, small tablet
    Large,      // Large tablet
    ExtraLarge  // Desktop, very large tablet
}

/**
 * Sprite flip options
 */
enum class SpriteFlip {
    None,
    Horizontal,
    Vertical,
    Both
}

/**
 * Sprite sheet configuration
 */
data class SpriteSheet(
    val frameWidth: Int,
    val frameHeight: Int,
    val totalFrames: Int,
    val framesPerRow: Int,
    val screenCategory: ScreenCategory = ScreenCategory.Medium
)

/**
 * Sprite specification with support for multiple screen sizes
 */
data class SpriteSpec(
    val spriteSheets: Map<ScreenCategory, SpriteSheet>,
    val imageBitmaps: Map<ScreenCategory, ImageBitmap>
) {
    // Convenience constructor for single sprite sheet with ImageBitmap
    constructor(
        spriteSheet: SpriteSheet,
        imageBitmap: ImageBitmap
    ) : this(
        spriteSheets = mapOf(spriteSheet.screenCategory to spriteSheet),
        imageBitmaps = mapOf(spriteSheet.screenCategory to imageBitmap)
    )

    // Get sprite sheet for current screen category
    fun getSpriteSheet(screenCategory: ScreenCategory): SpriteSheet {
        return spriteSheets[screenCategory]
            ?: spriteSheets.values.first() // Fallback to first available
    }

    // Get image bitmap for current screen category
    fun getImageBitmap(screenCategory: ScreenCategory): ImageBitmap {
        return imageBitmaps[screenCategory]
            ?: imageBitmaps.values.first() // Fallback to first available
    }
}

/**
 * Builder class for creating SpriteSpec from drawable resources
 */
class SpriteSpecBuilder {
    private val spriteSheets = mutableMapOf<ScreenCategory, SpriteSheet>()
    private val drawableResIds = mutableMapOf<ScreenCategory, Int>()

    /**
     * Add sprite sheet configuration for a specific screen category
     */
    fun addSpriteSheet(
        screenCategory: ScreenCategory = ScreenCategory.Medium,
        @DrawableRes drawableRes: Int,
        frameWidth: Int,
        frameHeight: Int,
        totalFrames: Int,
        framesPerRow: Int
    ): SpriteSpecBuilder {
        val spriteSheet = SpriteSheet(
            frameWidth = frameWidth,
            frameHeight = frameHeight,
            totalFrames = totalFrames,
            framesPerRow = framesPerRow,
            screenCategory = screenCategory
        )
        spriteSheets[screenCategory] = spriteSheet
        drawableResIds[screenCategory] = drawableRes
        return this
    }

    /**
     * Build SpriteSpec with ImageBitmaps loaded from drawable resources
     */
    @Composable
    fun build(): SpriteSpec {
        val imageBitmaps = mutableMapOf<ScreenCategory, ImageBitmap>()

        drawableResIds.forEach { (screenCategory, drawableRes) ->
            imageBitmaps[screenCategory] = imageResource(drawableRes)
        }

        return SpriteSpec(spriteSheets, imageBitmaps)
    }

    /**
     * Build SpriteSpec with ImageBitmaps loaded from drawable resources (non-composable version)
     */
    fun build(context: Context): SpriteSpec {
        val imageBitmaps = mutableMapOf<ScreenCategory, ImageBitmap>()

        drawableResIds.forEach { (screenCategory, drawableRes) ->
            imageBitmaps[screenCategory] = context.imageResource(drawableRes)
        }

        return SpriteSpec(spriteSheets, imageBitmaps)
    }
}