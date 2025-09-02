package com.acnteam.crashybird.presentation.screen.play.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import com.acnteam.crashybird.R
import androidx.compose.ui.graphics.Brush
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent
import com.acnteam.crashybird.presentation.ui.theme.Pink40
import com.acnteam.crashybird.presentation.ui.theme.White

@Composable
fun FirefliesUI(
    collected: Int,
    required: Int
) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .padding(all = 24.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        OrangeAccent,
                        BrownPrimary,
                    )
                )
            )
            .border(2.dp, Pink40, RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.dom_con),
            contentDescription = "Firefly Icon",
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "$collected / $required",
            color = White,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
