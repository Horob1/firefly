package com.acnteam.crashybird.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavScreen {
    @Serializable
    data object Welcome : NavScreen()

    @Serializable
    data object Home : NavScreen()

    @Serializable
    data object Play : NavScreen()

    @Serializable
    data object Setting : NavScreen()

    @Serializable
    data object User : NavScreen()
}