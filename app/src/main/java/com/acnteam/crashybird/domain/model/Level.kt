package com.acnteam.crashybird.domain.model

import androidx.annotation.DrawableRes
import com.acnteam.crashybird.R

sealed class Level(val levelNumber: Int, @DrawableRes val image: Int,@DrawableRes val movingImage: Int,@DrawableRes val pipeImage: Int,@DrawableRes val pipeCapImage: Int) {
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

    data object One : Level(
        1,
        R.drawable.forest_background,
        R.drawable.forest_moving_background,
        R.drawable.forest_pipe,
        R.drawable.forest_pipe_cap
    )

    data object Two : Level(
        2,
        R.drawable.lava_background,
        R.drawable.lava_moving_background,
        R.drawable.forest_pipe,
        R.drawable.forest_pipe_cap
    )

    data object Three : Level(
        3,
        R.drawable.ice_backgroud,
        R.drawable.ice_moving_background,
        R.drawable.forest_pipe,
        R.drawable.forest_pipe_cap
    )

    data object Four : Level(4,
        R.drawable.desert_background,
        R.drawable.desert_moving_background,
        R.drawable.forest_pipe,
        R.drawable.forest_pipe_cap
    )

    data object Five : Level(5,
        R.drawable.city_background,
        R.drawable.city_moving_background,
        R.drawable.forest_pipe,
        R.drawable.forest_pipe_cap
    )
}