package com.acnteam.crashybird.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.game.Skill

sealed class Character(
    val name: String,
    @DrawableRes val sprite: Int,
    val frameWidth: Int,
    val frameHeight: Int,
    @StringRes val desc: Int,
    val coin: Int = 0,
    val skills: List<Skill> = emptyList()
) {
    companion object {
        fun getByName(name: String): Character {
            return when (name) {
                Jack.name -> Jack
                Oii.name -> Oii
                Virus.name -> Virus
                Bee.name -> Bee
                GrandFather.name -> GrandFather
                Messi.name -> Messi
                Ronaldo.name -> Ronaldo
                Naruto.name -> Naruto
                Vinicius.name -> Vinicius
                SonTung.name -> SonTung
                Sasuke.name -> Sasuke
                Luffy.name -> Luffy
                else -> Jack
            }
        }

        fun getAll(): List<Character> {
            return listOf(Jack, Oii, Virus, Bee, GrandFather, Messi, Ronaldo, Naruto, Vinicius, SonTung, Sasuke, Luffy)
        }
    }

    data object Jack :
        Character(
            "Jack",
            R.drawable.jack,
            frameWidth = 160,
            frameHeight = 103,
            R.string.desc_jack,
            skills = listOf()
        )

    data object Oii : Character(
        "Oii",
        R.drawable.domixi,
        frameWidth = 200,
        frameHeight = 84,
        R.string.desc_oii,
        coin = 1000,
        skills = listOf(Skill.HIGH_JUMP)
    )

    data object Naruto : Character(
        "Naruto",
        R.drawable.naruto,
        frameWidth = 122,
        frameHeight = 125,
        R.string.desc_naruto,
        coin = 3600,
        skills = listOf(Skill.HIGH_JUMP,  Skill.SLOW)
    )


    data object Virus : Character(
        "Virus",
        R.drawable.virus,
        frameWidth = 94,
        frameHeight = 108,
        R.string.desc_virus,
        coin = 3600,
        skills = listOf(Skill.HIGH_JUMP,  Skill.SLOW)
    )
    data object Sasuke : Character(
        "Sasuke",
        R.drawable.sasuke,
        frameWidth = 93,
        frameHeight = 130,
        R.string.desc_sasuke,
        coin = 3600,
        skills = listOf(Skill.HIGH_JUMP,  Skill.SLOW)
    )

    data object GrandFather : Character(
        "GrandFather Teacher",
        R.drawable.quylao,
        frameWidth = 120,
        frameHeight = 132,
        R.string.desc_grand_father,
        coin = 3600,
        skills = listOf(Skill.HIGH_JUMP,  Skill.SLOW)
    )
    data object Bee : Character(
        "Bee",
        R.drawable.bee_sprite,
        frameWidth = 80,
        frameHeight = 80,
        R.string.desc_bee,
        coin = 10000,
        skills = listOf(Skill.HIGH_JUMP, Skill.DASH, Skill.SLOW)
    )

    data object Messi : Character(
        "Messi",
        R.drawable.messi,
        frameWidth = 90,
        frameHeight = 117,
        R.string.desc_messi,
        coin = 10000,
        skills = listOf(Skill.HIGH_JUMP, Skill.DASH, Skill.SLOW)
    )

    data object Ronaldo : Character(
        "Ronaldo",
        R.drawable.ronaldo,
        frameWidth = 93,
        frameHeight = 120,
        R.string.desc_ronaldo,
        coin = 10000,
        skills = listOf(Skill.HIGH_JUMP, Skill.DASH, Skill.SLOW)
    )

    data object Vinicius : Character(
        "Vinicius",
        R.drawable.vinicius,
        frameWidth = 92,
        frameHeight = 140,
        R.string.desc_vinicius,
        coin = 10000,
        skills = listOf(Skill.HIGH_JUMP, Skill.DASH, Skill.SLOW)
    )

    data object SonTung : Character(
        "Son Tung-MTP",
        R.drawable.sontung_mtp,
        frameWidth = 66,
        frameHeight = 107,
        R.string.desc_sontung,
        coin = 10000,
        skills = listOf(Skill.HIGH_JUMP, Skill.DASH, Skill.SLOW)
    )

    data object Luffy : Character(
        "Luffy",
        R.drawable.luffy,
        frameWidth = 97,
        frameHeight = 142,
        R.string.desc_luffy,
        coin = 10000,
        skills = listOf(Skill.HIGH_JUMP, Skill.DASH, Skill.SLOW)
    )
}