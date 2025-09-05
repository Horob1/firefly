package com.acnteam.crashybird.domain.model

import androidx.annotation.DrawableRes
import com.acnteam.crashybird.R

sealed class Items(
    val name: String,
    @DrawableRes val image: Int,
    val price: Int,
    val itemWidth: Int,
    val itemHeight: Int,
) {
    companion object {
        fun getByName(name: String): Items {
            return when (name) {
                ChildFirefly.name -> ChildFirefly
                DragonBall.name -> DragonBall
                Shit.name -> Shit
                Fox.name -> Fox
                BallDor.name ->BallDor
                Meal.name -> Meal
                WorldCup.name -> WorldCup
                Noodles.name -> Noodles
                else -> ChildFirefly
            }
        }

        fun getAll(): List<Items> {
            return listOf(ChildFirefly, DragonBall,BallDor)
        }
    }

    object ChildFirefly : Items(
        name = "Dom Con",
        image = R.drawable.item_domcon,
        price = 100,
        itemWidth = 100,
        itemHeight = 100,
    )

    object WorldCup : Items(
        name = "WorldCup",
        image = R.drawable.item_worldcup,
        price = 100,
        itemWidth = 100,
        itemHeight = 150,
    )

    object Noodles : Items(
        name = "Noodles",
        image = R.drawable.item_milixon,
        price = 100,
        itemWidth = 100,
        itemHeight = 150,
    )

    object Fox : Items(
        name = "Fox",
        image = R.drawable.item_fox,
        price = 100,
        itemWidth = 100,
        itemHeight = 150,
    )


    object Meal : Items(
        name = "Meal",
        image = R.drawable.item_meal,
        price = 100,
        itemWidth = 100,
        itemHeight = 150,
    )

    object DragonBall : Items(
        name = "Dragon Ball",
        image = R.drawable.item_dragon_ball,
        price = 100,
        itemWidth = 100,
        itemHeight = 150,
        )

    object Shit : Items(
        name = "Shit",
        image = R.drawable.item_shit,
        price = 100,
        itemWidth = 100,
        itemHeight = 67,
    )

    object BallDor : Items(
        name = "Ball D'or",
        image = R.drawable.item_balldor,
        price = 100,
        itemWidth = 100,
        itemHeight = 145,
    )
}