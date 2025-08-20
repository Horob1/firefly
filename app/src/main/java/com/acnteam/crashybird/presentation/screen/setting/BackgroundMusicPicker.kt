package com.acnteam.crashybird.presentation.screen.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.BackgroundMusic
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.White

@Preview
@Composable
fun BackgroundMusicPicker(
    modifier: Modifier = Modifier,
    @StringRes title: Int = R.string.txt_background_music,
    musicList: List<BackgroundMusic> = BackgroundMusic.getAlls(),
    currentMusic: BackgroundMusic = BackgroundMusic.BacPhan,
    onMusicChange: (BackgroundMusic) -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = BrownPrimary.copy(
                alpha = 0.3f
            )
        ),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(title),
                modifier = modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = White,
                    fontStyle = FontStyle.Italic,
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    count = musicList.size,
                    key = { musicList[it].name }
                ) {
                    BgMusicCard(
                        music = musicList[it],
                        isPlaying = musicList[it] == currentMusic,
                        onClick = { onMusicChange(musicList[it]) }
                    )
                }
            }
        }
    }
}