package com.acnteam.crashybird.presentation.screen.home.signin

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel() : ViewModel() {
    private val googleAuthClient = GoogleAuthClient()
    private val _authUiState = MutableStateFlow<SignInState<UserData>>(
        if (googleAuthClient.isSignedIn() && googleAuthClient.getSignedInUser() != null) {
            SignInState.Authenticated(googleAuthClient.getSignedInUser()!!)
        } else {
            SignInState.UnAuthenticated
        }
    )
    val authUiState: StateFlow<SignInState<UserData>> = _authUiState

    fun logIn(activity: Activity) {
        _authUiState.value = SignInState.Loading
        viewModelScope.launch {
            val result = googleAuthClient.signIn(activity)
            if (result && googleAuthClient.getSignedInUser() != null) {
                _authUiState.value = SignInState.Authenticated(googleAuthClient.getSignedInUser()!!)
            } else {
                _authUiState.value = SignInState.Error("Sign in failed")
            }
        }
    }

    fun reload() {
        val user = googleAuthClient.getSignedInUser()
        if (user != null) {
            _authUiState.value = SignInState.Authenticated(user)
        } else {
            _authUiState.value = SignInState.UnAuthenticated
        }
    }

    fun clearUiState() {
        _authUiState.value = SignInState.UnAuthenticated
    }
}
