package com.acnteam.crashybird.presentation.screen.home

import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.LevelState

data class HomeState(
    val levels: List<LevelState>,
    val coin: Int,
    val currentLevel: Level,
)