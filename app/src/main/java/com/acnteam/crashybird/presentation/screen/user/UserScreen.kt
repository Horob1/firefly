package com.acnteam.crashybird.presentation.screen.user

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.acnteam.crashybird.R
import com.acnteam.crashybird.presentation.common.ConfirmOrCancelDialog
import com.acnteam.crashybird.presentation.common.CrashyBirdButton
import com.acnteam.crashybird.presentation.common.NotificationAlertDialog
import com.acnteam.crashybird.presentation.common.TripleOrbitLoadingAnimation
import com.acnteam.crashybird.presentation.screen.home.signin.SignInState
import com.acnteam.crashybird.presentation.ui.theme.Black
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    onToRankingsPage: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: UserViewModel,
) {
    val context = LocalActivity.current
    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val showSyncDialog = remember {
        mutableStateOf(false)
    }
    val showConfirmSyncUp = remember {
        mutableStateOf(false)
    }

    val showConFirmSyncDown = remember {
        mutableStateOf(false)
    }

    val showSyncSuccess = remember {
        mutableStateOf(false)
    }

    val showSyncFail = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(authUiState) {
        if (authUiState is SignInState.UnAuthenticated) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.txt_profile)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrownPrimary,
                    navigationIconContentColor = White,
                    titleContentColor = White
                )
            )
        }
    ) {
        if (authUiState is SignInState.Authenticated)
            Column(
                modifier = Modifier
                    .background(
                        Black
                    )
                    .fillMaxSize()
                    .padding(it)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val userInfo = remember(authUiState) {
                    (authUiState as SignInState.Authenticated).data
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(160.dp)
                            .height(160.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            rememberAsyncImagePainter(
                                model = userInfo.profilePictureUrl
                            ),
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    userInfo.username?.let { text ->
                        Text(
                            text = text,
                            color = White,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(2f / 3f)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CrashyBirdButton(
                        onClick = {
                            onToRankingsPage()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = "Ranks",
                                tint = White
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = stringResource(R.string.txt_rankings),
                                color = White,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    CrashyBirdButton(
                        onClick = {
                            // Sync
                            showSyncDialog.value = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync",
                                tint = White
                            )

                            Spacer(Modifier.width(8.dp))


                            Text(
                                text = stringResource(R.string.txt_sync),
                                color = White,
                                style = MaterialTheme.typography.titleLarge
                            )

                        }
                    }

                }

                Spacer(Modifier.weight(1f))

                CrashyBirdButton(
                    onClick = {
                        viewModel.logout(context as Activity, callback = {
                            onBackClick()
                        })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Text(
                        stringResource(R.string.btn_logout),
                        color = White,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

            }
        else Box(
            modifier = Modifier
                .background(
                    Black
                )
                .fillMaxSize()
                .padding(it)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            TripleOrbitLoadingAnimation(
                modifier = Modifier.size(160.dp)
            )
        }
        if (showSyncDialog.value) {
            Dialog(
                onDismissRequest = {
                    showSyncDialog.value = false
                },
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = BrownPrimary
                    )

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CrashyBirdButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                showSyncDialog.value = false
                                showConFirmSyncDown.value = true
                            },
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically

                            ) {
                                Icon(
                                    Icons.Default.ArrowDownward,
                                    contentDescription = "Sync Down",
                                    tint = White
                                )

                                Text(
                                    text = stringResource(R.string.txt_sync_down),
                                    color = White,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        CrashyBirdButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                showSyncDialog.value = false
                                showConfirmSyncUp.value = true
                            },
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ArrowUpward,
                                    contentDescription = "Sync Up",
                                    tint = White
                                )
                                Text(
                                    text = stringResource(R.string.txt_sync_up),
                                    color = White,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showConFirmSyncDown.value) {
            ConfirmOrCancelDialog(
                message = R.string.desc_sync_down,
                title = R.string.txt_sync,
                onConfirm = {
                    showConFirmSyncDown.value = false
                    viewModel.syncDown(
                        onSuccess = {
                            showSyncSuccess.value = true
                        },
                        onFail = {
                            showConFirmSyncDown.value = false
                        }
                    )
                },
                onDismiss = {
                    showConFirmSyncDown.value = false
                    showSyncDialog.value = true
                }
            )
        }

        if (showConfirmSyncUp.value) {
            ConfirmOrCancelDialog(
                message = R.string.desc_sync_up,
                title = R.string.txt_sync,
                onConfirm = {
                    showConfirmSyncUp.value = false
                    viewModel.syncToServer(
                        onSuccess = {
                            showSyncSuccess.value = true
                        },
                        onFail = {
                            showSyncFail.value = true
                        }
                    )
                },
                onDismiss = {
                    showConfirmSyncUp.value = false
                    showSyncDialog.value = true
                }
            )
        }

        if (showSyncSuccess.value)
            NotificationAlertDialog(
                mess = R.string.alert_sync_success,
                onDismiss = {
                    showSyncSuccess.value = false
                }
            )

        if (showSyncFail.value)
            NotificationAlertDialog(
                mess = R.string.alert_sync_fail,
                onDismiss = {
                    showSyncFail.value = false
                }
            )
    }
}