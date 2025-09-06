package com.acnteam.crashybird.domain.model.game

import androidx.compose.ui.geometry.Rect

data class BoostItemState(
    val boostItem: BoostItem,
    var x: Float,
    var y: Float
) {
    fun toRect(): Rect {
        return Rect(
            left = x - boostItem.width / 2f,
            top = y - boostItem.height / 2f,
            right = x + boostItem.width / 2f,
            bottom = y + boostItem.height / 2f
        )
    }
}