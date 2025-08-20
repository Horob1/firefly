package com.acnteam.crashybird.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.acnteam.crashybird.data.remote.dto.LevelDto

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val levelNumber: Int,
    val isUnlocked: Boolean,
    val highScore: Int = 0,
) {
    fun toLevelDto(): LevelDto {
        return LevelDto(
            levelNumber = levelNumber,
            isUnlocked = isUnlocked,
            highScore = highScore
        )
    }
}