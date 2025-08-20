package com.acnteam.crashybird.presentation.common

import kotlinx.serialization.Serializable

@Serializable
sealed class Level(val levelNumber: Int) {

    @Serializable
    data object One : Level(1)

    @Serializable
    data object Two : Level(2)

    @Serializable
    data object Three : Level(3)

    @Serializable
    data object Four : Level(4)

    @Serializable
    data object Five : Level(5)
}