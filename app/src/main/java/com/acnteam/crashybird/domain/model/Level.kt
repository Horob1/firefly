package com.acnteam.crashybird.domain.model

import androidx.annotation.DrawableRes
import com.acnteam.crashybird.R

sealed class Level(val levelNumber: Int, @DrawableRes val image: Int,@DrawableRes val movingImage: Int,@DrawableRes val pipeImage: Int,@DrawableRes val pipeCapImage: Int, val fireFlyRequire: Int) {
    companion object {
        fun getByLevelNumber(levelNumber: Int): Level {
            return when (levelNumber) {
                One.levelNumber -> One
                Two.levelNumber -> Two
                Three.levelNumber -> Three
                Four.levelNumber -> Four
                Five.levelNumber -> Five
                Six.levelNumber -> Six
                else -> One
            }
        }

        fun getAll(): List<Level> {
            return listOf(One, Two, Three, Four, Five, Six)
        }
    }

    data object One : Level(
        1,
        R.drawable.forest_background,
        R.drawable.forest_moving_background,
        R.drawable.forest_pipe,
        R.drawable.forest_pipe_cap,
        fireFlyRequire = 9
    )

    data object Two : Level(
        2,
        R.drawable.lava_background,
        R.drawable.lava_moving_background,
        R.drawable.lava_pipe,
        R.drawable.lava_pipe_cap,
        fireFlyRequire = 3
    )

    data object Three : Level(
        3,
        R.drawable.ice_backgroud,
        R.drawable.ice_moving_background,
        R.drawable.ice_pipe,
        R.drawable.ice_pipe_cap,
        fireFlyRequire = 3
    )

    data object Four : Level(4,
        R.drawable.desert_background,
        R.drawable.desert_moving_background,
        R.drawable.forest_pipe,
        R.drawable.forest_pipe_cap,
        fireFlyRequire = 3
    )

    data object Five : Level(5,
        R.drawable.city_background,
        R.drawable.city_moving_background,
        R.drawable.forest_pipe,
        R.drawable.forest_pipe_cap,
        fireFlyRequire = 3
    )

    data object Six : Level(6,
        R.drawable.dragon_palace_background,
        R.drawable.dragon_palace_moving_background,
        R.drawable.forest_pipe,
        R.drawable.forest_pipe_cap,
        fireFlyRequire = 3
    )
}