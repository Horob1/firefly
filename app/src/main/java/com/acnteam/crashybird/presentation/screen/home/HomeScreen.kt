package com.acnteam.crashybird.presentation.screen.home

import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.LevelState
import com.acnteam.crashybird.presentation.common.TextCard
import com.acnteam.crashybird.presentation.common.UiState
import com.acnteam.crashybird.presentation.navigation.NavScreen
import com.acnteam.crashybird.presentation.ui.theme.Black
import com.acnteam.crashybird.utils.center

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    val context = LocalContext.current
    val selectedLevelUiState by viewModel.currentLevelUiState.collectAsStateWithLifecycle()
    val coinUiState by viewModel.coinUiState.collectAsStateWithLifecycle()
    val levelsUiState by viewModel.levelsUiState.collectAsStateWithLifecycle()


    Scaffold {
        if (selectedLevelUiState !is UiState.Success || coinUiState !is UiState.Success || levelsUiState !is UiState.Success) {
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
                CircularProgressIndicator()
            }
        } else {
            val selectedLevel = (selectedLevelUiState as UiState.Success<Level>).data
            val levels = (levelsUiState as UiState.Success<List<LevelState>>).data
            val coin = (coinUiState as UiState.Success<Int>).data
            (levelsUiState as UiState.Success<List<LevelState>>).data

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Image(
                    painter = painterResource(id = selectedLevel.image),
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
                        TextCard(
                            string = "Horob1 Siu Hoo",
                            enabled = true,
                            onClick = { /*TODO*/ }
                        )

                        // Coin

                        TextCard(
                            string = "${stringResource(R.string.txt_coin)}: ${
                                coin.toString().center(8)
                            }",
                        )

                    }


                    // Banner
                    Banner(stringResource(R.string.app_name))

                    Spacer(modifier = Modifier.height(100.dp))

                    // Game Star Control
                    GamePanel(
                        selectedMap = selectedLevel,
                        levels = levels,
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
                        onPlayClick = {

                        },
                        onSettingClick = {
                            navController.navigate(NavScreen.Setting)
                        }
                    )

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}