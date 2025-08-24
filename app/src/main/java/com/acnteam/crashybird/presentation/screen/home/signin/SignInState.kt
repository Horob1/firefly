package com.acnteam.crashybird.presentation.screen.home.signin


interface SignInState<out T> {
    data object UnAuthenticated : SignInState<Nothing>
    data class Authenticated<T>(val data: T) : SignInState<T>
    data object Loading : SignInState<Nothing>
    data class Error(val message: String) : SignInState<Nothing>
}