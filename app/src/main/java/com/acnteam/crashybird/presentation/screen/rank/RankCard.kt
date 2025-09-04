package com.acnteam.crashybird.presentation.screen.rank

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.RankState
import com.acnteam.crashybird.presentation.ui.theme.Black
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent
import com.acnteam.crashybird.presentation.ui.theme.White

@Preview
@Composable
fun RankCard(
    modifier: Modifier = Modifier,
    rankState: RankState = RankState(
        username = "Username",
        score = 100,
        level = Level.One,
    ),
    onClick: (Int) -> Unit = {}
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = OrangeAccent,
        ),
        onClick = {
            onClick(rankState.level.levelNumber)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(2 / 3f)
                    .height(160.dp)
                    .clip(
                        MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = rankState.level.image),
                    contentDescription = rankState.username,
                    contentScale = ContentScale.FillHeight,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            shape = MaterialTheme.shapes.small
                        )
                )
            }

            Spacer(
                modifier = Modifier
                    .width(8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${stringResource(R.string.txt_level)} ${rankState.level.levelNumber}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Black,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Black
                    ),
                )

                Text(
                    text = rankState.username,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = White,
                        textAlign = TextAlign.Start
                    ),
                )

                Text(
                    text = rankState.score.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = BrownPrimary,
                        fontWeight = FontWeight.Black
                    )
                )
            }
        }
    }
}