package com.acnteam.crashybird.data.remote.dto

data class UserDto(
    var coin: Int = 0,
    var levels: List<LevelDto> = emptyList(),
    var characters: List<CharacterDto> = emptyList(),
    var items: List<ItemDto> = emptyList(),
)