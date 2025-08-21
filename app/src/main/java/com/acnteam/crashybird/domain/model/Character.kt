package com.acnteam.crashybird.domain.model

import androidx.annotation.DrawableRes
import com.acnteam.crashybird.R
import kotlinx.serialization.Serializable

@Serializable
sealed class Character(
    val name: String,
    @DrawableRes val sprite: Int,
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
    data object Jack : Character("Jack", R.drawable.lock)

    @Serializable
    data object Oii : Character("Oii", R.drawable.lock)

    @Serializable
    data object Bee : Character("Bee", R.drawable.lock)

    @Serializable
    data object Virus : Character("Virus", R.drawable.lock)
}