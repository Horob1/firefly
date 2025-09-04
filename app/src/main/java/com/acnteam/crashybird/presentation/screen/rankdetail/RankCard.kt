package com.acnteam.crashybird.presentation.screen.rankdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.acnteam.crashybird.R

@Preview(showBackground = true)
@Composable
fun RankCard(
    modifier: Modifier = Modifier,
    rank: Int? = 1,
    name: String = "Name",
    score: Int = 0,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                color = Color.White.copy(
                    0.5f
                )
            ).border(
                width = 2.dp,
                color = Color.White
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
        ) {

        Box(
            modifier = Modifier.size(80.dp).padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if(rank != null) {
                val rankResource =
                    when (rank) {
                        0 -> R.drawable.rank_1
                        1 -> R.drawable.rank_2
                        2 -> R.drawable.rank_3
                        3 -> R.drawable.rank_4
                        else -> R.drawable.rank_5
                    }

                Image(
                    painter = painterResource(id = rankResource),
                    contentDescription = "rank of $name",
                    modifier = Modifier.fillMaxSize()
                )
            }
            else {
                Text(
                    "...",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                name,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Box(
            modifier = Modifier.padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                score.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}