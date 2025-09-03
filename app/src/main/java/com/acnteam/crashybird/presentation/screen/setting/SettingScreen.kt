package com.acnteam.crashybird.presentation.screen.setting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.BackgroundMusic
import com.acnteam.crashybird.presentation.ui.theme.Black
import com.acnteam.crashybird.presentation.ui.theme.BrownPrimary
import com.acnteam.crashybird.presentation.ui.theme.White

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    viewModel: SettingViewModel,
) {
    val backgroundMusicSettings by viewModel.backgroundMusicSettings.collectAsStateWithLifecycle()
    val soundEffectSettings by viewModel.soundEffectSettings.collectAsStateWithLifecycle()
    val graphicMode by viewModel.graphicMode.collectAsStateWithLifecycle()
    val musicList = remember {
        BackgroundMusic.getAlls()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.txt_setting)) },
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
            MusicVolumePicker(
                title = R.string.txt_background_music,
                sliderValue = backgroundMusicSettings.volume,
                onValueChange = { value ->
                    viewModel.setBackgroundMusicSettings(
                        backgroundMusicSettings.copy(volume = value)
                    )
                },
                isEnabled = backgroundMusicSettings.isEnabled,
                toggleEnabled = {
                    viewModel.setBackgroundMusicSettings(
                        backgroundMusicSettings.copy(isEnabled = !backgroundMusicSettings.isEnabled)
                    )
                }
            )

            MusicVolumePicker(
                title = R.string.txt_effect_music,
                sliderValue = soundEffectSettings.volume,
                onValueChange = { value ->
                    viewModel.setSoundEffectSettings(
                        soundEffectSettings.copy(volume = value)
                    )
                },
                isEnabled = soundEffectSettings.isEnabled,
                toggleEnabled = {
                    viewModel.setSoundEffectSettings(
                        soundEffectSettings.copy(isEnabled = !soundEffectSettings.isEnabled)
                    )
                }
            )

            GraphicPicker(
                currentGraphicMode = graphicMode,
                onGraphicModeChange = { graphicM ->
                    viewModel.setGraphicMode(graphicM)
                }
            )

            BackgroundMusicPicker(
                title = R.string.txt_background_music,
                musicList = musicList,
                currentMusic = backgroundMusicSettings.music,
                onMusicChange = {
                    viewModel.setBackgroundMusicSettings(
                        backgroundMusicSettings.copy(music = it)
                    )
                }
            )
        }
    }
}