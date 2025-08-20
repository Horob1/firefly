package com.acnteam.crashybird.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.acnteam.crashybird.data.repository.OnBoardingRepositoryImpl.PreferencesKey.FIRST_TIME_IN_HOME
import com.acnteam.crashybird.data.repository.OnBoardingRepositoryImpl.PreferencesKey.ON_BOARDING_KEY
import com.acnteam.crashybird.domain.repository.OnBoardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.onBoardingDataStore: DataStore<Preferences> by preferencesDataStore(name = OnBoardingRepositoryImpl.ON_BOARDING_STORE)

class OnBoardingRepositoryImpl(context: Context): OnBoardingRepository {
    companion object {
        const val ON_BOARDING_STORE = "on_boarding_store"
    }

    private object PreferencesKey {
         val ON_BOARDING_KEY = booleanPreferencesKey("on_boarding_key")
         val FIRST_TIME_IN_HOME = booleanPreferencesKey("first_time_in_home")
    }

    private val dataStore = context.onBoardingDataStore

    override fun getOnBoardingState(): Flow<Boolean> {
        return dataStore.data.map {
            it[ON_BOARDING_KEY] ?: false
        }
    }

    override suspend fun saveOnBoardingState(completed: Boolean) {
        dataStore.edit {
            it[ON_BOARDING_KEY] = completed
        }
    }

    override fun getFirstTimeInHome(): Flow<Boolean> {
        return dataStore.data.map {
            it[FIRST_TIME_IN_HOME] ?: false
        }
    }

    override suspend fun saveFirstTimeInHome(completed: Boolean) {
        dataStore.edit {
            it[FIRST_TIME_IN_HOME] = completed
        }
    }
}