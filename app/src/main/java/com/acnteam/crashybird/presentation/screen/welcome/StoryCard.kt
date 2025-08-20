package com.acnteam.crashybird.presentation.screen.welcome

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.acnteam.crashybird.R
import com.acnteam.crashybird.presentation.ui.theme.White

@Composable
fun StoryCard(
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    @StringRes title: Int,
    @DrawableRes image: Int
    ) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = stringResource(id = title),
                contentScale = ContentScale.FillWidth,
                modifier = modifier.fillMaxWidth().height(height)
            )
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = White
                ),
                modifier = Modifier.padding(16.dp).align(Alignment.BottomStart)
            )
        }
    }
}

@Preview
@Composable
fun StoryCardPreview() {
    StoryCard(
        title = R.string.story_1_title,
        image = R.drawable.story_3
    )
}