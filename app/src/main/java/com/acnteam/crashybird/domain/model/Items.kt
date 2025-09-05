package com.acnteam.crashybird.domain.model

import androidx.annotation.DrawableRes
import com.acnteam.crashybird.R

sealed class Items(
    val name: String,
    @DrawableRes val image: Int,
    //val price: Int,
    //...
) {
    companion object {
        fun getByName(name: String): Items {
            return when (name) {
                ChildFirefly.name -> ChildFirefly
                WorldCup.name -> WorldCup
                else -> ChildFirefly
            }

        }

        fun getAll(): List<Items> {
            return listOf(ChildFirefly, WorldCup)
        }
    }

    object ChildFirefly : Items(
        name = "Child Firefly",
        image = R.drawable.dom_con
    )

    object WorldCup : Items(
        name = "World Cup",
        image = R.drawable.dom_con
    )
}