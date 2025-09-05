package com.acnteam.crashybird.domain.repository

import com.acnteam.crashybird.domain.model.CharacterState
import com.acnteam.crashybird.domain.model.ItemState
import com.acnteam.crashybird.domain.model.LevelState

interface UserRepository {
    suspend fun syncLocalToServer(
        coin: Int,
        levels: List<LevelState>,
        chars: List<CharacterState>,
        items: List<ItemState>,
    ): Result<Unit>

    suspend fun getUserDataFromServer(): Pair<Pair<Int, List<LevelState>>, Pair<List<CharacterState>, List<ItemState>>>
}