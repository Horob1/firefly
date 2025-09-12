package com.acnteam.crashybird.domain.model

import androidx.annotation.DrawableRes
import com.acnteam.crashybird.R

sealed class Enemy(
    val name: String,
    val height: Int,
    val width: Int,
    val radius: Int,
    val speed: Int,
    @DrawableRes val image: Int,
) {

    object ShishaDog : Enemy(
        "shishaDog",
        171,
        243,
        20,
        20,
        R.drawable.cho_hut_shisha
    )
}