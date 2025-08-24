package com.acnteam.crashybird.domain.repository

import com.acnteam.crashybird.domain.model.CharacterState
import com.acnteam.crashybird.domain.model.LevelState

interface UserRepository {
    suspend fun syncLocalToServer(
        coin: Int,
        levels: List<LevelState>,
        chars: List<CharacterState>,
    ): Result<Unit>

    suspend fun getUserDataFromServer(): Triple<Int, List<LevelState>, List<CharacterState>>
}