package com.acnteam.crashybird.domain.local

import com.acnteam.crashybird.data.local.entity.ItemEntity
import com.acnteam.crashybird.domain.model.Items
import kotlinx.coroutines.flow.Flow

interface ItemLocalDataSource {
    fun getItems(): Flow<List<ItemEntity>>

    suspend fun saveItems(items: List<ItemEntity>)

    suspend fun unlock(item: Items)

    suspend fun deleteAll()
}