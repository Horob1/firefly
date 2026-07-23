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
        MediaPlayer.create(context, musicResId)?.apply {
            isLooping = true
            setVolume(volume, volume)
        }
    }

    // Release mediaPlayer when musicResId changes or Composable is disposed
    DisposableEffect(mediaPlayer) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    // Lắng nghe vòng đời để pause/play khi app nền
    DisposableEffect(lifecycleOwner, mediaPlayer, isPlaying) {
        val observer = LifecycleEventObserver { _, event ->
            mediaPlayer?.let { player ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> if (isPlaying && !player.isPlaying) {
                        player.start()
                    }

                    Lifecycle.Event.ON_PAUSE -> if (player.isPlaying) {
                        player.pause()
                    }

                    else -> Unit
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(mediaPlayer, isPlaying) {
        mediaPlayer?.let { player ->
            if (isPlaying) {
                if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    if (!player.isPlaying) player.start()
                }
            } else {
                if (player.isPlaying) player.pause()
            }
        }
    }

    LaunchedEffect(mediaPlayer, volume) {
        mediaPlayer?.setVolume(volume, volume)
    }
}
