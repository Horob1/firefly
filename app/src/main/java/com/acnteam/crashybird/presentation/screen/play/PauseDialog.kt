import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.acnteam.crashybird.R
import com.acnteam.crashybird.presentation.common.CrashyBirdButton
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.GrayDark
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent
import com.acnteam.crashybird.presentation.ui.theme.White

@Composable
fun PauseDialog(
    onDismiss: () -> Unit,
    onHomeClick: () -> Unit,
    onResumeClick: () -> Unit
) {
    var isMusicOn by remember { mutableStateOf(true) }
    var isSoundOn by remember { mutableStateOf(true) }

    AlertDialog(
        containerColor = GrayDark,
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tiêu đề
                Text(
                    text = "PAUSE",
                    style = MaterialTheme.typography.titleLarge,
                    color = White
                )

                DividerGradient(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    thickness = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            OrangeAccent,
                            BrownPrimary
                        )
                    )
                )


                // Hàng nút
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CrashyBirdButton(
                        onClick = { onHomeClick() },
                        paddingVertical = 12,
                        paddingHorizontal = 12
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home button",
                            modifier = Modifier.size(36.dp),
                            tint = White
                        )
                    }

                    CrashyBirdButton(
                        onClick = { isSoundOn = !isSoundOn },
                        paddingVertical = 12,
                        paddingHorizontal = 12
                    ) {
                        Icon(
                            imageVector = if (isSoundOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Sound button",
                            modifier = Modifier.size(36.dp),
                            tint = White
                        )
                    }

                    CrashyBirdButton(
                        onClick = { isMusicOn = !isMusicOn },
                        paddingVertical = 12,
                        paddingHorizontal = 12
                    ) {
                        Icon(
                            imageVector = if (isMusicOn) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = "Music button",
                            modifier = Modifier.size(36.dp),
                            tint = White
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CrashyBirdButton(
                        modifier = Modifier.width(240.dp),
                        onClick = onResumeClick,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Resume",
                                style = MaterialTheme.typography.titleLarge,
                                color = White
                            )
                            Icon(Icons.Default.Replay, contentDescription = "Setting button", tint = White)
                        }
                    }
                }
            }
        }
    )
}
@Composable
fun DividerGradient(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    brush: Brush,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(brush = brush)
    )
}

