package com.acnteam.crashybird.presentation.screen.user

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acnteam.crashybird.domain.usecase.SyncDownUseCase
import com.acnteam.crashybird.domain.usecase.SyncToServerUseCase
import com.acnteam.crashybird.presentation.screen.home.signin.GoogleAuthClient
import com.acnteam.crashybird.presentation.screen.home.signin.SignInState
import com.acnteam.crashybird.presentation.screen.home.signin.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val syncToServerUseCase: SyncToServerUseCase,
    private val syncDownUseCase: SyncDownUseCase,
) : ViewModel() {
    private val googleAuthClient = GoogleAuthClient()
    private val _authUiState = MutableStateFlow<SignInState<UserData>>(
        if (googleAuthClient.isSignedIn() && googleAuthClient.getSignedInUser() != null) {
            SignInState.Authenticated(googleAuthClient.getSignedInUser()!!)
        } else {
            SignInState.Error("User not authenticated")
        }
    )
    val authUiState: StateFlow<SignInState<UserData>> = _authUiState

    fun logout(context: Activity, callback: () -> Unit) {
        viewModelScope.launch {
            googleAuthClient.signOut(context)
            _authUiState.value = SignInState.UnAuthenticated
            callback()
        }
    }

    fun syncToServer(
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        viewModelScope.launch {
            val result = syncToServerUseCase()
            if (result.isSuccess) {
                onSuccess()
            } else {
                onFail()
            }
        }
    }

    fun syncDown(
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        viewModelScope.launch {
            syncDownUseCase()
            onSuccess()
        }
    }
}