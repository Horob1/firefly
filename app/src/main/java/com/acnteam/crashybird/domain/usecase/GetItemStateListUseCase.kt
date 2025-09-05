package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.local.ItemLocalDataSource
import com.acnteam.crashybird.domain.model.ItemState
import com.acnteam.crashybird.domain.model.Items
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetItemStateListUseCase(
    private val itemRepository: ItemLocalDataSource,
) {
    operator fun invoke(): Flow<List<ItemState>> = itemRepository.getItems().map {
        it.map { itemEntity ->
            ItemState(
                item = Items.getByName(itemEntity.name),
                isUnlocked = itemEntity.isUnlocked,
            )
        }
    }
}