package com.acnteam.crashybird.presentation.screen.home

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.acnteam.crashybird.R
import com.acnteam.crashybird.presentation.common.ConfirmOrCancelDialog
import com.acnteam.crashybird.presentation.common.NotificationAlertDialog
import com.acnteam.crashybird.presentation.common.TextCard
import com.acnteam.crashybird.presentation.common.TripleOrbitLoadingAnimation
import com.acnteam.crashybird.presentation.common.UiState
import com.acnteam.crashybird.presentation.navigation.NavScreen
import com.acnteam.crashybird.presentation.screen.home.signin.SignInState
import com.acnteam.crashybird.presentation.screen.home.signin.SignInViewModel
import com.acnteam.crashybird.presentation.ui.theme.Black
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent
import com.acnteam.crashybird.presentation.ui.theme.White
import com.acnteam.crashybird.utils.center
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.component.ShowcaseStyle
import com.canopas.lib.showcase.component.rememberIntroShowcaseState

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    authViewModel: SignInViewModel,
) {
    val context = LocalContext.current
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val showUnlockDialog = remember {
        mutableStateOf(false)
    }
    val showUnlockSuccess = remember {
        mutableStateOf(false)
    }
    val showUnlockFail = remember {
        mutableStateOf(false)
    }
    val showNotUnlock = remember {
        mutableStateOf(false)
    }
    val showExitDialog = remember {
        mutableStateOf(false)
    }

    BackHandler {
        showExitDialog.value = true
    }

    Scaffold {
        if (homeUiState is UiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(
                        color = Black
                    ),
                contentAlignment = Alignment.Center
            )
            {
                TripleOrbitLoadingAnimation(
                    modifier = Modifier.size(160.dp)
                )
            }
        } else {
            val homeState = (homeUiState as UiState.Success<HomeState>).data

            val introShowcaseState = rememberIntroShowcaseState()
            IntroShowcase(
                showIntroShowCase = !homeState.showCaseCompleted,
                dismissOnClickOutside = false,
                onShowCaseCompleted = {
                    viewModel.completeShowCase()
                },
                state = introShowcaseState,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    Image(
                        painter = painterResource(id = homeState.currentLevel.image),
                        contentDescription = "Background",
                        modifier = Modifier
                            .fillMaxSize(),
                        alignment = Alignment.Center,
                        contentScale = ContentScale.FillHeight
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Black.copy(alpha = 0.3f))

                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        // User info
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Name and user setting

                            val userState by authViewModel.authUiState.collectAsStateWithLifecycle()
                            LaunchedEffect(
                                key1 = userState,
                            ) {
                                if (userState is SignInState.Error) {
                                    Toast.makeText(
                                        context,
                                        (userState as SignInState.Error).message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    authViewModel.clearUiState()
                                }
                            }

                            LaunchedEffect(Unit) {
                                authViewModel.reload()
                            }

                            Box(
                                modifier = Modifier.introShowCaseTarget(
                                    index = 4,
                                    style = ShowcaseStyle.Default.copy(
                                        backgroundColor = OrangeAccent,
                                        backgroundAlpha = 0.98f,
                                        targetCircleColor = White
                                    ),
                                    content = {
                                        Text(
                                            stringResource(R.string.desc_signin),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = Black
                                            )
                                        )
                                    }
                                )

                            ) {
                                when (userState) {
                                    is SignInState.Authenticated -> {
                                        TextCard(
                                            string = if (userState is SignInState.Authenticated) {
                                                (userState as SignInState.Authenticated).data.username!!
                                            } else {
                                                stringResource(R.string.btn_sign_in)
                                            },
                                            enabled = true,
                                            onClick = {
                                                navController.navigate(NavScreen.User)
                                            }
                                        )
                                    }

                                    is SignInState.UnAuthenticated -> {
                                        TextCard(
                                            string = stringResource(R.string.btn_sign_in),
                                            enabled = true,
                                            onClick = {
                                                authViewModel.logIn(context as Activity)
                                            },
                                        )
                                    }

                                    else -> {
                                        TripleOrbitLoadingAnimation(
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }

                                }
                            }

                            // Coin

                            Box(
                                modifier = Modifier.introShowCaseTarget(
                                    index = 3,
                                    style = ShowcaseStyle.Default.copy(
                                        backgroundColor = OrangeAccent,
                                        backgroundAlpha = 0.98f,
                                        targetCircleColor = White
                                    ),
                                    content = {
                                        Text(
                                            stringResource(R.string.desc_coin),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = Black
                                            )
                                        )
                                    }
                                )
                            ) {
                                TextCard(
                                    string = "${stringResource(R.string.txt_coin)}: ${
                                        homeState.coin.toString().center(8)
                                    }",
                                )
                            }

                        }


                        // Banner
                        Banner(stringResource(R.string.app_name))

                        Spacer(modifier = Modifier.height(100.dp))

                        // Game Star Control
                        GamePanel(
                            selectedMap = homeState.currentLevel,
                            levels = homeState.levels,
                            selectedCharacter = homeState.currentCharacter,
                            characterList = homeState.characterList,
                            onLeftMapClick = {
                                viewModel.onLeftMapClick {
                                    Toast.makeText(
                                        context,
                                        context.getText(R.string.alert_map_is_not_unlock),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onRightMapClick = {
                                viewModel.onRightMapClick {
                                    Toast.makeText(
                                        context,
                                        context.getText(R.string.alert_map_is_not_unlock),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onUnlockClick = {
                                showUnlockDialog.value = true
                            },
                            onLeftCharacterClick = {
                                viewModel.onLeftCharacterClick()
                            },
                            onRightCharacterClick = {
                                viewModel.onRightCharacterClick()
                            },
                            onPlayClick = {
                                viewModel.onPlayClick(
                                    onSuccess = {
                                        navController.navigate(
                                            NavScreen.Play(
                                                levelNumber = homeState.currentLevel.levelNumber,
                                                characterName = homeState.currentCharacter.name
                                            )
                                        )
                                    },
                                    onFail = {
                                        showNotUnlock.value = true
                                    }
                                )
                            },
                            onSettingClick = {
                                navController.navigate(NavScreen.Setting)
                            }
                        )

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }

                if (showUnlockDialog.value) {
                    UnlockDialog(
                        character = homeState.currentCharacter,
                        onDismiss = {
                            showUnlockDialog.value = false
                        },
                        onConfirm = {
                            showUnlockDialog.value = false
                            viewModel.unlockCharacter(
                                onSuccess = {
                                    showUnlockSuccess.value = true
                                },
                                onFail = {
                                    showUnlockFail.value = true
                                }
                            )

                        },
                    )
                }

                if (showUnlockSuccess.value) {
                    NotificationAlertDialog(
                        onDismiss = {
                            showUnlockSuccess.value = false
                        },
                        R.string.alert_unlock_success,
                    )
                }

                if (showUnlockFail.value) {
                    NotificationAlertDialog(
                        onDismiss = {
                            showUnlockFail.value = false
                        },
                        R.string.alert_is_not_enough_coin,
                    )

                }
                if (showNotUnlock.value) {
                    NotificationAlertDialog(
                        onDismiss = {
                            showNotUnlock.value = false
                        },
                        R.string.alert_character_is_not_unlock,
                    )
                }

                if (showExitDialog.value) {
                    ConfirmOrCancelDialog(
                        onConfirm = {
                            (context as Activity).finish()
                        },
                        onDismiss = {
                            showExitDialog.value = false
                        },
                        title = R.string.txt_exit_game,
                        message = R.string.desc_exit_game
                    )
                }
            }
        }
    }
}