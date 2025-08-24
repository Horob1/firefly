package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.repository.OnBoardingRepository
import kotlinx.coroutines.flow.Flow


class GetFirstTimeInHomeState(
    private val onBoardingRepository: OnBoardingRepository,
) {
    operator fun invoke(): Flow<Boolean> = onBoardingRepository.getFirstTimeInHome()
}