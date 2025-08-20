package com.acnteam.crashybird.presentation.navigation

import android.media.MediaPlayer
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun BackgroundMusicPlayer(
    @RawRes musicResId: Int,
    isPlaying: Boolean,
    volume: Float = 0.5f,
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val mediaPlayer = remember(musicResId) {
        MediaPlayer.create(context, musicResId).apply {
            isLooping = true
            setVolume(volume, volume)
        }
    }

    // Lắng nghe vòng đời để pause/play khi app nền
    DisposableEffect(lifecycleOwner, mediaPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> if (isPlaying && !mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                }

                Lifecycle.Event.ON_PAUSE -> if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mediaPlayer.release()
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        } else if (!isPlaying && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    LaunchedEffect(volume) {
        mediaPlayer.setVolume(volume, volume)
    }
}
