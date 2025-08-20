package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.repository.OnBoardingRepository
import kotlinx.coroutines.flow.Flow

class GetOnBoardingStateUseCase(
    private val onBoardingRepository: OnBoardingRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return onBoardingRepository.getOnBoardingState()
    }
}