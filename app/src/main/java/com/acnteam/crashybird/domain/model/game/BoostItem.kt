package com.acnteam.crashybird.domain.model.game

import com.acnteam.crashybird.R

data class BoostItem(
    val id: Int,
    val image: Int,
    val width: Int,
    val height: Int,
)

val mini_item = BoostItem(id = 1, image = R.drawable.aptx_4869, width = 150, height = 155)
val big_item = BoostItem(id = 2, image = R.drawable.boom, width = 120, height = 131)
val immortal = BoostItem(id = 3, image = R.drawable.daotien, width = 120, height = 121)
