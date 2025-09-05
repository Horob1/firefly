package com.acnteam.crashybird.presentation.screen.shop

import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.CharacterState
import com.acnteam.crashybird.domain.model.ItemState
import com.acnteam.crashybird.domain.model.Items

data class ShopUiState(
    val coin: Int,
    val characters: List<CharacterState>,
    val items: List<ItemState>,
    val currentCharacter: Character,
    val currentItem: Items,
)