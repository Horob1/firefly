package com.acnteam.crashybird.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.usecase.GetOnBoardingStateUseCase
import com.acnteam.crashybird.presentation.navigation.NavScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val getOnBoardingStateUseCase: GetOnBoardingStateUseCase
): ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _startScreen = MutableStateFlow<NavScreen>(NavScreen.Home)
    val startScreen: StateFlow<NavScreen> = _startScreen

    init {
       viewModelScope.launch {
           getOnBoardingStateUseCase().collect { completed ->
               if (completed) {
                   _startScreen.value = NavScreen.Home
               } else {
                   _startScreen.value = NavScreen.Welcome
               }
           }
           _isLoading.value = false
       }
    }
}