package com.acnteam.crashybird.presentation.screen.rank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.model.RankState
import com.acnteam.crashybird.domain.usecase.GetRanksUseCase
import com.acnteam.crashybird.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RankViewModel(
    private val getRanksUseCase: GetRanksUseCase,
) : ViewModel() {
    private val _rankStateUi = MutableStateFlow<UiState<List<RankState>>>(
        UiState.Loading
    )

    val rankStateUi: StateFlow<UiState<List<RankState>>> = _rankStateUi

    fun getRanks() {
        _rankStateUi.value = UiState.Loading
        viewModelScope.launch {
            _rankStateUi.value = UiState.Success(
                getRanksUseCase()
            )
        }
    }
}