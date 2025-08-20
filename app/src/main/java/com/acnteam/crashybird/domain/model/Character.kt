package com.acnteam.crashybird.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Character(
    val name: String,
) {
    companion object {
        fun getByName(name: String): Character {
            return when (name) {
                Jack.name -> Jack
                Oii.name -> Oii
                Bee.name -> Bee
                Virus.name -> Virus
                else -> Jack
            }
        }

        fun getAll(): List<Character> {
            return listOf(Jack, Oii, Bee, Virus)
        }
    }

    @Serializable
    data object Jack : Character("Jack")

    @Serializable
    data object Oii : Character("Oii")

    @Serializable
    data object Bee : Character("Bee")

    @Serializable
    data object Virus : Character("Virus")
}