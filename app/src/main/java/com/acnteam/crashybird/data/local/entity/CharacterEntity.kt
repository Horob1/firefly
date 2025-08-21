package com.acnteam.crashybird.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.acnteam.crashybird.data.remote.dto.CharacterDto
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.CharacterState

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val isUnlocked: Boolean,
) {
    fun toCharacterDto(): CharacterDto {
        return CharacterDto(
            name = name,
            isUnlocked = isUnlocked
        )
    }

    fun toCharacterState(): CharacterState {
        return CharacterState(
            character = Character.getByName(name),
            isUnlocked = isUnlocked
        )
    }

}