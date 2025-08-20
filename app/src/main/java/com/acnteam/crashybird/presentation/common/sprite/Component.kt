package com.acnteam.crashybird.presentation.common.sprite

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

/**
 * Composable function which is used to display and animate the
 * Sprite Sheet within a composable hierarchy.
 *
 * @param modifier Optional modifier.
 * @param spriteState [SpriteState] manages the state of a sprite sheet animation.
 * @param spriteSpec [SpriteSpec] sprite sheet specification.
 * @param spriteFlip Use it to flip the sprite Vertically, Horizontally or both.
 */
@Composable
fun SpriteView(
    modifier: Modifier = Modifier,
    spriteState: SpriteState,
    spriteSpec: SpriteSpec,
    spriteFlip: SpriteFlip = SpriteFlip.None
) {
    val screenCategory = getCurrentScreenCategory()
    val spriteSheet = spriteSpec.getSpriteSheet(screenCategory)
    val spriteImage = spriteSpec.getImageBitmap(screenCategory)

    val currentFrame = spriteState.currentFrame.collectAsState().value

    // Get the row and column position based on the current frame
    val row by rememberUpdatedState(
        newValue = currentFrame / spriteState.framesPerRow
    )
    val column by rememberUpdatedState(
        newValue = currentFrame % spriteState.framesPerRow
    )

    val frameSize by remember(spriteSheet) {
        derivedStateOf {
            IntSize(
                spriteSheet.frameWidth,
                spriteSheet.frameHeight
            )
        }
    }

    val frameOffset by remember(row, column, frameSize) {
        derivedStateOf {
            IntOffset(
                x = column * frameSize.width,
                y = row * frameSize.height
            )
        }
    }

    Canvas(
        modifier = modifier
            .width(with(LocalDensity.current) { frameSize.width.toDp() })
            .height(with(LocalDensity.current) { frameSize.height.toDp() })
    ) {
        val dstOffsetX = (size.width - frameSize.width.toFloat()) / 2
        val dstOffsetY = (size.height - frameSize.height.toFloat()) / 2

        withTransform(
            transformBlock = {
                if (spriteFlip != SpriteFlip.None) {
                    scale(
                        scaleX = when (spriteFlip) {
                            SpriteFlip.Both -> -1f
                            SpriteFlip.Horizontal -> -1f
                            else -> 1f
                        },
                        scaleY = when (spriteFlip) {
                            SpriteFlip.Both -> -1f
                            SpriteFlip.Vertical -> -1f
                            else -> 1f
                        },
                        pivot = when (spriteFlip) {
                            SpriteFlip.Horizontal -> {
                                Offset(
                                    x = dstOffsetX + frameSize.width / 2f,
                                    y = dstOffsetY
                                )
                            }
                            SpriteFlip.Vertical -> {
                                Offset(
                                    x = dstOffsetX,
                                    y = dstOffsetY + frameSize.height / 2f
                                )
                            }
                            else -> {
                                Offset(
                                    x = dstOffsetX + frameSize.width / 2f,
                                    y = dstOffsetY + frameSize.height / 2f
                                )
                            }
                        }
                    )
                }
            }
        ) {
            drawImage(
                image = spriteImage,
                srcOffset = frameOffset,
                srcSize = frameSize,
                dstOffset = IntOffset(
                    dstOffsetX.toInt(),
                    dstOffsetY.toInt()
                ),
                dstSize = frameSize
            )
        }
    }
}

/**
 * Composable function to create SpriteView directly from drawable resource
 *
 * @param modifier Optional modifier.
 * @param spriteState [SpriteState] manages the state of a sprite sheet animation.
 * @param drawableRes Drawable resource ID (R.drawable.sprite_sheet)
 * @param frameWidth Width of each frame in pixels
 * @param frameHeight Height of each frame in pixels
 * @param totalFrames Total number of frames in the sprite sheet
 * @param framesPerRow Number of frames per row
 * @param spriteFlip Use it to flip the sprite Vertically, Horizontally or both.
 * @param screenCategory Screen category for responsive design
 */
@Composable
fun SpriteView(
    modifier: Modifier = Modifier,
    spriteState: SpriteState,
    @DrawableRes drawableRes: Int,
    frameWidth: Int,
    frameHeight: Int,
    totalFrames: Int,
    framesPerRow: Int,
    spriteFlip: SpriteFlip = SpriteFlip.None,
    screenCategory: ScreenCategory = ScreenCategory.Medium
) {
    val spriteSpec = spriteSpecFromResource(
        drawableRes = drawableRes,
        frameWidth = frameWidth,
        frameHeight = frameHeight,
        totalFrames = totalFrames,
        framesPerRow = framesPerRow,
        screenCategory = screenCategory
    )

    SpriteView(
        modifier = modifier,
        spriteState = spriteState,
        spriteSpec = spriteSpec,
        spriteFlip = spriteFlip
    )
}

/**
 * Function which is used to draw a sprite sheet inside the DrawScope of the Canvas.
 *
 * @param spriteState Control the sprite sheet animation.
 * @param spriteSpec Sprite sheet specification with optional parameters for various screen dimensions.
 * @param currentFrame The current frame of the sprite sheet animation.
 * @param image Sprite sheet image that needs to be drawn on the canvas.
 * @param offset An optional offset.
 * @param spriteFlip Use it to flip the sprite Vertically, Horizontally or both.
 */
fun DrawScope.drawSpriteView(
    spriteState: SpriteState,
    spriteSpec: SpriteSpec,
    currentFrame: Int,
    image: ImageBitmap,
    offset: IntOffset = IntOffset.Zero,
    spriteFlip: SpriteFlip = SpriteFlip.None
) {
    // Get the row and column position based on the current frame
    val row = currentFrame / spriteState.framesPerRow
    val column = currentFrame % spriteState.framesPerRow

    val frameSize = IntSize(
        spriteSpec.spriteSheets.values.first().frameWidth,
        spriteSpec.spriteSheets.values.first().frameHeight
    )

    val frameOffset = IntOffset(
        x = column * frameSize.width,
        y = row * frameSize.height
    )

    withTransform(
        transformBlock = {
            if (spriteFlip != SpriteFlip.None) {
                scale(
                    scaleX = when (spriteFlip) {
                        SpriteFlip.Both -> -1f
                        SpriteFlip.Horizontal -> -1f
                        else -> 1f
                    },
                    scaleY = when (spriteFlip) {
                        SpriteFlip.Both -> -1f
                        SpriteFlip.Vertical -> -1f
                        else -> 1f
                    },
                    pivot = when (spriteFlip) {
                        SpriteFlip.Horizontal -> {
                            Offset(
                                x = offset.x + frameSize.width / 2f,
                                y = offset.y.toFloat()
                            )
                        }
                        SpriteFlip.Vertical -> {
                            Offset(
                                x = offset.x.toFloat(),
                                y = offset.y + frameSize.height / 2f
                            )
                        }
                        else -> {
                            Offset(
                                x = offset.x + frameSize.width / 2f,
                                y = offset.y + frameSize.height / 2f
                            )
                        }
                    }
                )
            }
        }
    ) {
        drawImage(
            image = image,
            srcOffset = frameOffset,
            srcSize = frameSize,
            dstOffset = offset,
            dstSize = frameSize
        )
    }
}