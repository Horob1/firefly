package com.acnteam.crashybird.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.acnteam.crashybird.data.remote.dto.ItemDto
import com.acnteam.crashybird.domain.model.ItemState
import com.acnteam.crashybird.domain.model.Items


@Entity(tableName = "items")
class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val isUnlocked: Boolean,
) {
    fun toItem(): ItemState {
        return ItemState(
            item = Items.getByName(name),
            isUnlocked = isUnlocked
        )
    }

    fun toItemDto(): ItemDto {
        return ItemDto(
            name = name,
            isUnlocked = isUnlocked
        )
    }

}