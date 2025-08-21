package com.acnteam.crashybird.presentation.common.sprite.domain

import androidx.annotation.DrawableRes


data class SpriteSheet(
    val frameWidth: Int,
    val frameHeight: Int,
    @DrawableRes val imageRes: Int
)