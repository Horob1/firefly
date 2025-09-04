package com.acnteam.crashybird.presentation.screen.rankdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.model.RankState
import com.acnteam.crashybird.domain.usecase.FetchMyRankOfLevelUseCase
import com.acnteam.crashybird.domain.usecase.FetchRankOfLevelUseCase
import com.acnteam.crashybird.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RankDetailViewModel(
    private val fetchRankOfLevelUseCase: FetchRankOfLevelUseCase,
    private val fetchMyRankOfLevelUseCase: FetchMyRankOfLevelUseCase,
) : ViewModel() {
    private val _rankList = MutableStateFlow<UiState<List<RankState>>>(
        UiState.Loading
    )

    val rankList = _rankList

    private val _myRank = MutableStateFlow<UiState<RankState>>(
        UiState.Loading
    )

    val myRank = _myRank


    fun fetchRankOfLevel(levelNumber: Int) {
        viewModelScope.launch {
            val rankList = fetchRankOfLevelUseCase(levelNumber)
            _rankList.value = UiState.Success(rankList)
            val myRank = fetchMyRankOfLevelUseCase(levelNumber)
            _myRank.value = UiState.Success(myRank)
        }
    }

}
