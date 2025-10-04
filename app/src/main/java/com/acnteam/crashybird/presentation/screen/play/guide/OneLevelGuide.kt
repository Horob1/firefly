package com.acnteam.crashybird.presentation.screen.play.guide


import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acnteam.crashybird.R
import kotlinx.coroutines.delay

@Composable
fun LevelIntroPage(
    levelNumber: Int,
    lines: List<String>,
    onDismiss: () -> Unit
) {
    var showAll by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    val displayedLines = remember { mutableStateListOf<String>() }

    LaunchedEffect(levelNumber, showAll) {
        displayedLines.clear()
        finished = false
        if (showAll) {
            // Hiện full ngay
            displayedLines.addAll(lines)
            finished = true
        } else {
            // Hiệu ứng typing từng dòng
            for (line in lines) {
                var currentLine = ""
                for (char in line) {
                    currentLine += char
                    if (!showAll) {
                        displayedLines.add(currentLine)
                        delay(40) // tốc độ gõ chữ
                        displayedLines.removeAt(displayedLines.lastIndex)
                    } else break
                }
                displayedLines.add(line) // thêm dòng đầy đủ
                delay(500) // delay giữa các dòng
            }
            finished = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(24.dp)
            .clickable {
                if (!finished) {
                    showAll = true
                } else {
                    onDismiss()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "${stringResource(R.string.text_welcome_to_level)} $levelNumber",
                color = Color.Yellow,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            displayedLines.forEach { line ->
                Text(
                    text = line,
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (finished) {
                Text(
                    text = stringResource(R.string.text_ontap),
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

fun getIntroLinesForLevel(context: Context, level: Int): List<String> {
    return when (level) {
        1 -> listOf(
            context.getString(R.string.level1_line1),
            context.getString(R.string.level1_line2),
            context.getString(R.string.level1_line3),
            context.getString(R.string.level1_line4),
            context.getString(R.string.level1_line5),
            context.getString(R.string.level1_line6),
        )

        2 -> listOf(
            context.getString(R.string.level2_line1),
            context.getString(R.string.level2_line2),
            context.getString(R.string.level2_line3),
            context.getString(R.string.level2_line4),
            context.getString(R.string.level2_line5),
            context.getString(R.string.level2_line6),
            context.getString(R.string.level2_line7)
        )

        3 -> listOf(
            context.getString(R.string.level3_line1),
            context.getString(R.string.level3_line2),
            context.getString(R.string.level3_line3),
            context.getString(R.string.level3_line4),
            context.getString(R.string.level3_line5),
            context.getString(R.string.level3_line6),
            context.getString(R.string.level3_line7),
            context.getString(R.string.level3_line8)
        )

        4 -> listOf(
            context.getString(R.string.level4_line1),
            context.getString(R.string.level4_line2),
            context.getString(R.string.level4_line3),
            context.getString(R.string.level4_line4),
            context.getString(R.string.level4_line5),
            context.getString(R.string.level4_line6),
            context.getString(R.string.level4_line7),
            context.getString(R.string.level4_line8)
        )

        5 -> listOf(
            context.getString(R.string.level5_line1),
            context.getString(R.string.level5_line2),
            context.getString(R.string.level5_line3),
            context.getString(R.string.level5_line4),
            context.getString(R.string.level5_line5),
            context.getString(R.string.level5_line6),
            context.getString(R.string.level5_line7),
            context.getString(R.string.level5_line8)
        )

        6 -> listOf(
            context.getString(R.string.level6_line1),
            context.getString(R.string.level6_line2),
            context.getString(R.string.level6_line3),
            context.getString(R.string.level6_line4),
            context.getString(R.string.level6_line5),
            context.getString(R.string.level6_line6),
            context.getString(R.string.level6_line8)
        )

        else -> listOf(context.getString(R.string.text_ontap))
    }
}

