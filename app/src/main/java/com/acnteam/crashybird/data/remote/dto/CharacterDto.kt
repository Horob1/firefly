package com.acnteam.crashybird.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterDto(
    val name: String,
    val isUnlocked: Boolean,
)

