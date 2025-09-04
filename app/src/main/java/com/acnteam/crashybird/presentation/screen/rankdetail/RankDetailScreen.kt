package com.acnteam.crashybird.presentation.screen.rankdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.acnteam.crashybird.R
import com.acnteam.crashybird.presentation.common.UiState

@Composable
fun RankDetailScreen(
    viewModel: RankDetailViewModel,
    levelNumber: Int,
) {
    LaunchedEffect(key1 = levelNumber) {
        viewModel.fetchRankOfLevel(levelNumber)
    }

    val rankList by viewModel.rankList.collectAsStateWithLifecycle()
    val myRank by viewModel.myRank.collectAsStateWithLifecycle()

    Scaffold {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val background = when (levelNumber) {
                1 -> R.drawable.forest_background
                2 -> R.drawable.lava_background
                3 -> R.drawable.ice_backgroud
                4 -> R.drawable.desert_background
                5 -> R.drawable.city_background
                else -> R.drawable.dragon_palace_background
            }

            Image(
                painter = painterResource(id = background),
                contentDescription = "background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillHeight,
                alignment = Alignment.Center
            )

            if (rankList is UiState.Success && myRank is UiState.Success) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = 10.dp,
                        alignment = Alignment.CenterVertically
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.rank_label),
                        contentDescription = "rank label",
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .size(120.dp)
                    )

                    val rankListData = (rankList as UiState.Success).data
                    val myRankData = (myRank as UiState.Success).data

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 10.dp,
                            alignment = Alignment.CenterVertically
                        )
                    ) {
                        items(rankListData.size) { index ->
                            val rank = rankListData[index]
                            RankCard(
                                rank = index,
                                name = rank.username,
                                score = rank.score,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Your rank",
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    RankCard(
                        rank = rankListData.indexOf(myRankData),
                        name = myRankData.username,
                        score = myRankData.score
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                CircularProgressIndicator()
            }
        }
    }
}