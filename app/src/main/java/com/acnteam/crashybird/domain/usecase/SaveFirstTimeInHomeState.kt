package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.domain.repository.OnBoardingRepository

class SaveFirstTimeInHomeState(
    private val onBoardingRepository: OnBoardingRepository,
) {
    suspend operator fun invoke(boolean: Boolean) =
        onBoardingRepository.saveFirstTimeInHome(boolean)
}