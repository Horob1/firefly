package com.acnteam.crashybird.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.acnteam.crashybird.R

sealed class Character(
    val name: String,
    @DrawableRes val sprite: Int,
    val frameWidth: Int,
    val frameHeight: Int,
    @StringRes val desc: Int,
    val coin: Int = 0,
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

    data object Jack :
        Character("Jack", R.drawable.jack, frameWidth = 160, frameHeight = 103, R.string.desc_jack)

    data object Oii : Character(
        "Oii",
        R.drawable.oii,
        frameWidth = 200,
        frameHeight = 84,
        R.string.desc_oii,
        coin = 10000
    )

    data object Bee : Character(
        "Bee",
        R.drawable.bee_sprite,
        frameWidth = 80,
        frameHeight = 80,
        R.string.desc_bee,
        coin = 1000
    )

    data object Virus : Character(
        "Virus",
        R.drawable.virus_sprite,
        frameWidth = 140,
        frameHeight = 160,
        R.string.desc_virus,
        coin = 5000
    )
}