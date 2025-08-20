package com.acnteam.crashybird.domain.model

import androidx.annotation.DrawableRes
import com.acnteam.crashybird.R
import kotlinx.serialization.Serializable

@Serializable
sealed class Level(val levelNumber: Int, @DrawableRes val image: Int) {
    companion object {
        fun getByLevelNumber(levelNumber: Int): Level {
            return when (levelNumber) {
                One.levelNumber -> One
                Two.levelNumber -> Two
                Three.levelNumber -> Three
                Four.levelNumber -> Four
                Five.levelNumber -> Five
                else -> One
            }
        }

        fun getAll(): List<Level> {
            return listOf(One, Two, Three, Four, Five)
        }
    }

    @Serializable
    data object One : Level(1, R.drawable.forest_background)

    @Serializable
    data object Two : Level(2, R.drawable.lava_background)

    @Serializable
    data object Three : Level(3, R.drawable.ice_backgroud)

    @Serializable
    data object Four : Level(4, R.drawable.desert_backgournd)

    @Serializable
    data object Five : Level(5, R.drawable.city_background)
}