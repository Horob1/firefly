package com.acnteam.crashybird.presentation.screen.rank

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.acnteam.crashybird.R
import com.acnteam.crashybird.presentation.common.TripleOrbitLoadingAnimation
import com.acnteam.crashybird.presentation.common.UiState
import com.acnteam.crashybird.presentation.ui.theme.Black
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankScreen(
    onBackClick: () -> Unit,
    viewModel: RankViewModel,
    onClick: (Int) -> Unit,
) {

    LaunchedEffect(Unit) {
        viewModel.getRanks()
    }

    val rankStateUi by viewModel.rankStateUi.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.txt_rankings)) },
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
        Column(
            modifier = Modifier
                .background(
                    Black
                )
                .fillMaxSize()
                .padding(it)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (rankStateUi !is UiState.Success) Box(
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
            } else {
                val rankState = remember(rankStateUi) {
                    (rankStateUi as UiState.Success).data
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(rankState.size, key = { rank -> rank }) { rank ->
                        val rankState = rankState[rank]
                        RankCard(
                            rankState = rankState,
                            onClick = onClick
                        )
                    }
                }
            }
        }
    }

}