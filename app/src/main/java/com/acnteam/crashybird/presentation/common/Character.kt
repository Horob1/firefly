package com.acnteam.crashybird.presentation.common

import kotlinx.serialization.Serializable

@Serializable
sealed class Character(
    val name: String,
) {
    @Serializable
    data object Jack : Character("Jack")

    @Serializable
    data object Oii : Character("Oii")

    @Serializable
    data object Bee : Character("Bee")

    @Serializable
    data object Virus : Character("Virus")
}