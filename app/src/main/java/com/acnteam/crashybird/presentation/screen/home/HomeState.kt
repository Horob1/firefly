package com.acnteam.crashybird.presentation.screen.home

import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.CharacterState
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.LevelState

data class HomeState(
    val levels: List<LevelState>,
    val coin: Int,
    val currentLevel: Level,
    val characterList: List<CharacterState>,
    val currentCharacter: Character,
    val showCaseCompleted: Boolean,
)

data class HomePartial(
    val levels: List<LevelState>,
    val coin: Int,
    val currentLevel: Level,
    val currentCharacter: Character,
    val characterList: List<CharacterState>,
)