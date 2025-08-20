package com.acnteam.crashybird.domain.repository

import kotlinx.coroutines.flow.Flow

interface OnBoardingRepository {
    fun getOnBoardingState(): Flow<Boolean>
    suspend fun saveOnBoardingState(completed: Boolean)

    fun getFirstTimeInHome(): Flow<Boolean>
    suspend fun saveFirstTimeInHome(completed: Boolean)
}