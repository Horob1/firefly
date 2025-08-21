package com.acnteam.crashybird.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.usecase.GetCoinUseCase
import com.acnteam.crashybird.domain.usecase.GetCurrentLevelUseCase
import com.acnteam.crashybird.domain.usecase.GetLevelStateListUseCase
import com.acnteam.crashybird.domain.usecase.InitDataUseCase
import com.acnteam.crashybird.domain.usecase.SetCurrentLevelUseCase
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
    private val setCurrentLevelUseCase: SetCurrentLevelUseCase,
    private val initUseCase: InitDataUseCase,
) : ViewModel() {

    init {
        viewModelScope.launch {
            initUseCase()
        }
    }

    val homeUiState: StateFlow<UiState<HomeState>> = combine(
        getLevelStateListUseCase(),
        getCoinUseCase(),
        getCurrentLevelUseCase()
    ) { levels, coin, currentLevel ->
        HomeState(
            levels = levels,
            coin = coin,
            currentLevel = currentLevel
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
}