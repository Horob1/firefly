package com.acnteam.crashybird.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.usecase.GetCharacterListUseCase
import com.acnteam.crashybird.domain.usecase.GetCoinUseCase
import com.acnteam.crashybird.domain.usecase.GetCurrentCharacterUseCase
import com.acnteam.crashybird.domain.usecase.GetCurrentLevelUseCase
import com.acnteam.crashybird.domain.usecase.GetFirstTimeInHomeState
import com.acnteam.crashybird.domain.usecase.GetLevelStateListUseCase
import com.acnteam.crashybird.domain.usecase.InitDataUseCase
import com.acnteam.crashybird.domain.usecase.SaveFirstTimeInHomeState
import com.acnteam.crashybird.domain.usecase.SetCurrentCharacterUseCase
import com.acnteam.crashybird.domain.usecase.SetCurrentLevelUseCase
import com.acnteam.crashybird.domain.usecase.UnlockCharacterUseCase
import com.acnteam.crashybird.domain.usecase.WithDrawCoinUseCase
import com.acnteam.crashybird.presentation.common.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    getLevelStateListUseCase: GetLevelStateListUseCase,
    getCoinUseCase: GetCoinUseCase,
    getCurrentLevelUseCase: GetCurrentLevelUseCase,
    getCurrentCharacterUseCase: GetCurrentCharacterUseCase,
    getCharacterListUseCase: GetCharacterListUseCase,
    getFirstTimeInHomeState: GetFirstTimeInHomeState,
    private val setCurrentCharacterUseCase: SetCurrentCharacterUseCase,
    private val setCurrentLevelUseCase: SetCurrentLevelUseCase,
    private val initUseCase: InitDataUseCase,
    private val unLockCharacterUseCase: UnlockCharacterUseCase,
    private val withDrawCoinUseCase: WithDrawCoinUseCase,
    private val saveFirstTimeInHomeState: SaveFirstTimeInHomeState,
) : ViewModel() {

    init {
        viewModelScope.launch {
            initUseCase()
        }
    }


    val homeUiState: StateFlow<UiState<HomeState>> = combine(
        combine(
            getLevelStateListUseCase(),
            getCoinUseCase(),
            getCurrentLevelUseCase(),
            getCurrentCharacterUseCase(),
            getCharacterListUseCase()
        ) { levels, coin, currentLevel, currentCharacter, characterList ->
            HomePartial(levels, coin, currentLevel, currentCharacter, characterList)
        },
        getFirstTimeInHomeState()
    ) { quint, showCase ->
        val (levels, coin, currentLevel, currentCharacter, characterList) = quint

        HomeState(
            levels = levels,
            coin = coin,
            currentLevel = currentLevel,
            currentCharacter = currentCharacter,
            characterList = characterList,
            showCaseCompleted = showCase
        )
    }
        .map<HomeState, UiState<HomeState>> { UiState.Success(it) }
        .onStart { emit(UiState.Loading) }
        .catch { e -> emit(UiState.Error(e.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )


    fun onLeftMapClick(
        errorCallback: () -> Unit,
    ) {
        val data = (homeUiState.value as UiState.Success<HomeState>).data
        val currentLevel = data.currentLevel
        val currentLevelIndex =
            data.levels.indexOfFirst { it.level.levelNumber == currentLevel.levelNumber }
        if (currentLevelIndex == 0 || currentLevelIndex == -1) {
            return
        }
        val newLevel = data.levels[currentLevelIndex - 1]
        if (newLevel.isUnlocked) {
            viewModelScope.launch {
                setCurrentLevelUseCase(newLevel.level)
            }
        } else {
            errorCallback()
        }
    }

    fun onRightMapClick(
        errorCallback: () -> Unit,
    ) {
        val data = (homeUiState.value as UiState.Success<HomeState>).data
        val currentLevel = data.currentLevel
        val currentLevelIndex =
            data.levels.indexOfFirst { it.level.levelNumber == currentLevel.levelNumber }
        if (currentLevelIndex == data.levels.size - 1 || currentLevelIndex == -1) {
            return
        }
        val newLevel = data.levels[currentLevelIndex + 1]
        if (newLevel.isUnlocked) {
            viewModelScope.launch {
                setCurrentLevelUseCase(newLevel.level)
            }
        } else {
            errorCallback()
        }
    }

    fun unlockCharacter(
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        val data = (homeUiState.value as UiState.Success<HomeState>).data
        if (data.coin < data.currentCharacter.coin) {
            onFail()
            return
        }
        viewModelScope.launch {
            val character = (homeUiState.value as UiState.Success<HomeState>).data.currentCharacter
            unLockCharacterUseCase(character)
            withDrawCoinUseCase(character.coin)
            onSuccess()
        }
    }

    fun onLeftCharacterClick() {
        val data = (homeUiState.value as UiState.Success<HomeState>).data
        val currentCharacter = data.currentCharacter
        val currentCharacterIndex =
            data.characterList.indexOfFirst { it.character.name == currentCharacter.name }
        viewModelScope.launch {
            when (currentCharacterIndex) {
                -1 -> return@launch
                0 -> {

                    setCurrentCharacterUseCase(
                        data.characterList[data.characterList.size - 1].character
                    )

                }

                else -> {

                    setCurrentCharacterUseCase(
                        data.characterList[currentCharacterIndex - 1].character
                    )
                }
            }
        }
    }

    fun onRightCharacterClick() {
        val data = (homeUiState.value as UiState.Success<HomeState>).data
        val currentCharacter = data.currentCharacter
        val currentCharacterIndex = data.characterList.indexOfFirst {
            it.character.name == currentCharacter.name
        }
        viewModelScope.launch {
            when (currentCharacterIndex) {
                -1 -> return@launch
                data.characterList.size - 1 -> {
                    setCurrentCharacterUseCase(data.characterList[0].character)
                }

                else -> {
                    setCurrentCharacterUseCase(data.characterList[currentCharacterIndex + 1].character)
                }
            }
        }
    }

    fun onPlayClick(
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        val data = (homeUiState.value as UiState.Success<HomeState>).data
        val currentCharacter = data.currentCharacter
        if (data.characterList.isEmpty()) {
            onFail()
            return
        }
        if (data.characterList.find { it.character.name == currentCharacter.name } == null) {
            onFail()
            return
        }
        if (data.characterList.find {
                it.character.name == currentCharacter.name
            }?.isUnlocked == false) {
            onFail()
            return
        }
        onSuccess()
    }

    fun completeShowCase() {
        viewModelScope.launch {
            saveFirstTimeInHomeState(true)
        }
    }

    fun unlockJack() {
        viewModelScope.launch {
            unLockCharacterUseCase(Character.Jack)
        }

    }
}