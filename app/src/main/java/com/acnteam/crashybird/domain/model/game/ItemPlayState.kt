package com.acnteam.crashybird.domain.model.game

import androidx.compose.ui.geometry.Rect
import com.acnteam.crashybird.domain.model.Items

data class ItemPlayState(
    val item: Items,
    val x: Float,
    val y: Float
) {
    fun toRect(): Rect {
        return Rect(
            left = x - item.itemWidth / 2f,
            top = y - item.itemHeight / 2f,
            right = x + item.itemWidth / 2f,
            bottom = y + item.itemHeight / 2f
        )
    }
}
