package com.acnteam.crashybird.presentation.screen.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.usecase.CompleteOnBoardingUseCase
import kotlinx.coroutines.launch

class WelcomeViewModel(
    private val completeOnBoardingUseCase: CompleteOnBoardingUseCase
): ViewModel() {
    fun markDoneOnBoarding() {
        viewModelScope.launch {
            completeOnBoardingUseCase()
        }
    }
}