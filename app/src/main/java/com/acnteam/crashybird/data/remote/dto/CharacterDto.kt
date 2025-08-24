package com.acnteam.crashybird.data.remote.dto

import com.acnteam.crashybird.domain.model.Character

data class CharacterDto(
    var name: String = Character.Oii.name,
    var isUnlocked: Boolean = false,
)

