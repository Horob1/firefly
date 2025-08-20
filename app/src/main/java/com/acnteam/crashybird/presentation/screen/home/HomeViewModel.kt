package com.acnteam.crashybird.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.LevelState
import com.acnteam.crashybird.domain.usecase.GetCoinUseCase
import com.acnteam.crashybird.domain.usecase.GetCurrentLevelUseCase
import com.acnteam.crashybird.domain.usecase.GetLevelStateListUseCase
import com.acnteam.crashybird.domain.usecase.InitDataUseCase
import com.acnteam.crashybird.domain.usecase.SetCurrentLevelUseCase
import com.acnteam.crashybird.presentation.common.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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

    val levelsUiState: StateFlow<UiState<List<LevelState>>> =
        getLevelStateListUseCase()
            .map<List<LevelState>, UiState<List<LevelState>>> { states ->
                UiState.Success(states)
            }
            .onStart { emit(UiState.Loading) }
            .catch { e -> emit(UiState.Error(e.message ?: "Unknown error")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState.Loading
            )

    val coinUiState: StateFlow<UiState<Int>> = getCoinUseCase().map<Int, UiState<Int>> { coin ->
        UiState.Success(coin)
    }
        .onStart {
            emit(UiState.Loading)
        }.catch { e ->
            emit(UiState.Error(e.message ?: "Unknown error"))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    val currentLevelUiState: StateFlow<UiState<Level>> =
        getCurrentLevelUseCase().map<Level, UiState<Level>> { level ->
            UiState.Success(level)
        }.onStart {
            emit(UiState.Loading)
        }.catch { e ->
            emit(UiState.Error(e.message ?: "Unknown error"))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )


    fun onLeftMapClick(
        errorCallback: () -> Unit,
    ) {
        if (currentLevelUiState.value is UiState.Success && levelsUiState.value is UiState.Success) {
            val currentLevel = (currentLevelUiState.value as UiState.Success).data
            val currentLevelIndex = (levelsUiState.value as UiState.Success).data.indexOf(
                (levelsUiState.value as UiState.Success).data.find { it.level.levelNumber == currentLevel.levelNumber }
            )
            if (currentLevelIndex > 0) {
                val newLevel = (levelsUiState.value as UiState.Success).data[currentLevelIndex - 1]
                if (!newLevel.isUnlocked) errorCallback()
                else viewModelScope.launch {
                    setCurrentLevelUseCase(newLevel.level)
                }
            }
        }
    }

    fun onRightMapClick(
        errorCallback: () -> Unit,
    ) {
        if (currentLevelUiState.value is UiState.Success && levelsUiState.value is UiState.Success) {
            val currentLevel = (currentLevelUiState.value as UiState.Success).data
            val currentLevelIndex = (levelsUiState.value as UiState.Success).data.indexOf(
                (levelsUiState.value as UiState.Success).data.find { it.level.levelNumber == currentLevel.levelNumber }
            )
            if (currentLevelIndex < (levelsUiState.value as UiState.Success).data.size - 1 || currentLevelIndex > -1) {
                val newLevel =
                    (levelsUiState.value as UiState.Success).data[currentLevelIndex + 1]
                if (!newLevel.isUnlocked) errorCallback()
                else viewModelScope.launch {
                    setCurrentLevelUseCase(newLevel.level)
                }
            }
        }
    }
}