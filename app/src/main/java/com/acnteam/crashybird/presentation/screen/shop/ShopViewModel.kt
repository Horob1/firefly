package com.acnteam.crashybird.presentation.screen.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.Items
import com.acnteam.crashybird.domain.usecase.GetCharacterListUseCase
import com.acnteam.crashybird.domain.usecase.GetCoinUseCase
import com.acnteam.crashybird.domain.usecase.GetCurrentCharacterUseCase
import com.acnteam.crashybird.domain.usecase.GetCurrentItemUseCase
import com.acnteam.crashybird.domain.usecase.GetItemStateListUseCase
import com.acnteam.crashybird.domain.usecase.SetCurrentCharacterUseCase
import com.acnteam.crashybird.domain.usecase.SetCurrentItemUseCase
import com.acnteam.crashybird.domain.usecase.UnlockCharacterUseCase
import com.acnteam.crashybird.domain.usecase.UnlockItemUseCase
import com.acnteam.crashybird.domain.usecase.WithDrawCoinUseCase
import com.acnteam.crashybird.presentation.common.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShopViewModel(
    getCoinUseCase: GetCoinUseCase,
    getCurrentItemUseCase: GetCurrentItemUseCase,
    getCurrentCharacterUseCase: GetCurrentCharacterUseCase,
    getItemStateListUseCase: GetItemStateListUseCase,
    getCharacterListUseCase: GetCharacterListUseCase,
    private val withDrawCoinUseCase: WithDrawCoinUseCase,
    private val unlockItemUseCase: UnlockItemUseCase,
    private val unlockCharacterUseCase: UnlockCharacterUseCase,
    private val setCurrentItemUseCase: SetCurrentItemUseCase,
    private val setCurrentCharacterUseCase: SetCurrentCharacterUseCase,
) : ViewModel() {

    val state = combine(
        getCoinUseCase(),
        getCurrentItemUseCase(),
        getCurrentCharacterUseCase(),
        getItemStateListUseCase(),
        getCharacterListUseCase()
    ) { coin, currentItem, currentChar, itemList, charList ->
        ShopUiState(
            coin,
            currentItem = currentItem,
            currentCharacter = currentChar,
            items = itemList,
            characters = charList
        )
    }.map<ShopUiState, UiState<ShopUiState>> {
        UiState.Success(
            it
        )
    }.onStart { emit(UiState.Loading) }
        .catch { e -> emit(UiState.Error(e.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun unlockCharacter(char: Character, onSuccess: () -> Unit = {}, onFail: () -> Unit = {}) {
        viewModelScope.launch {
            if (char.coin <= (state.value as UiState.Success<ShopUiState>).data.coin) {
                withDrawCoinUseCase(char.coin)
                unlockCharacterUseCase(char)
                onSuccess()
            } else {
                onFail()
            }

        }
    }

    fun unlockItem(item: Items, onSuccess: () -> Unit, onFail: () -> Unit) {
        viewModelScope.launch {
            if (item.price <= (state.value as UiState.Success<ShopUiState>).data.coin) {
                withDrawCoinUseCase(item.price)
                unlockItemUseCase(item)
                onSuccess()
            } else {
                onFail()
            }
        }
    }

    fun setCurrentItem(item: Items) {
        viewModelScope.launch {
            setCurrentItemUseCase(item)
        }
    }

    fun setCurrentCharacter(char: Character) {
        viewModelScope.launch {
            setCurrentCharacterUseCase(char)
        }
    }
}