package com.acnteam.crashybird.presentation.screen.home.signin

import android.app.Activity
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.acnteam.crashybird.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class GoogleAuthClient {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun isSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getSignedInUser(): UserData? = firebaseAuth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            profilePictureUrl = photoUrl?.toString()
        )
    }

    suspend fun signIn(activity: Activity): Boolean {
        if (isSignedIn()) return true

        return try {
            val result = buildCredentialRequest(activity)
            handleSignIn(result)
        } catch (e: Exception) {
            Log.d("GoogleAuthClient", "signIn: Exception + ${e.message}")
            if (e is CancellationException) throw e
            false
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): Boolean {
        val credential = result.credential

        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return try {
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(
                    tokenCredential.idToken, null
                )
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                authResult.user != null
            } catch (e: GoogleIdTokenParsingException) {
                Log.d(
                    "GoogleAuthClient",
                    "handleSignIn: GoogleIdTokenParsingException + ${e.message}"
                )
                false
            }
        } else {
            Log.d("GoogleAuthClient", "handleSignIn: Invalid credential type")
            return false
        }
    }

    private suspend fun buildCredentialRequest(activity: Activity): GetCredentialResponse {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(activity.getString(R.string.web_client_id))
                    .setAutoSelectEnabled(false)
                    .build()
            )
            .build()

        val credentialManager = CredentialManager.create(activity)
        return credentialManager.getCredential(
            request = request,
            context = activity
        )
    }

    suspend fun signOut(activity: Activity) {
        val credentialManager = CredentialManager.create(activity)
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        firebaseAuth.signOut()
    }
}
