package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.repository.OnBoardingRepository

class CompleteOnBoardingUseCase(
    private val repository: OnBoardingRepository
) {
    suspend operator fun invoke() = repository.saveOnBoardingState(true)
}