package com.acnteam.crashybird.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavScreen {
    @Serializable
    data object Welcome : NavScreen()

    @Serializable
    data object Home : NavScreen()

    @Serializable
    data class Play(val levelNumber: Int, val characterName: String) : NavScreen()

    @Serializable
    data object Setting : NavScreen()

    @Serializable
    data object User : NavScreen()

    @Serializable
    data object Rank : NavScreen()

    @Serializable
    data object Shop : NavScreen()

    @Serializable
    data class RankDetail(
        val levelNumber: Int,
    ) : NavScreen()

}