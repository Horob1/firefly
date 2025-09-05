package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.local.ItemLocalDataSource
import com.acnteam.crashybird.domain.model.Items

class UnlockItemUseCase(
    private val itemRepository: ItemLocalDataSource,
) {
    suspend operator fun invoke(item: Items) = itemRepository.unlock(item)
}