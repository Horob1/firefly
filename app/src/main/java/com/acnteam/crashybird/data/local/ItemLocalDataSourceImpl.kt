package com.acnteam.crashybird.data.local

import com.acnteam.crashybird.data.local.dao.ItemDao
import com.acnteam.crashybird.data.local.entity.ItemEntity
import com.acnteam.crashybird.domain.local.ItemLocalDataSource
import com.acnteam.crashybird.domain.model.Items
import kotlinx.coroutines.flow.Flow

class ItemLocalDataSourceImpl(
    private val itemDao: ItemDao,
) : ItemLocalDataSource {
    override fun getItems(): Flow<List<ItemEntity>> {
        return itemDao.getAllItems()
    }

    override suspend fun saveItems(items: List<ItemEntity>) {
        itemDao.insertItems(items)
    }

    override suspend fun unlock(item: Items) {
        itemDao.unlockItem(item.name, true)
    }

    override suspend fun deleteAll() {
        itemDao.deleteAllItems()
    }
}