package com.acnteam.crashybird.presentation.screen.play

import android.content.Context
import android.media.SoundPool
import com.acnteam.crashybird.R


class SoundEffectPlayer(context: Context) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .build()

    private var jumpSound: Int = soundPool.load(context, R.raw.jump, 1)
    private var fallSound: Int = soundPool.load(context, R.raw.falling, 1)
    private var gameOverSound: Int = soundPool.load(context, R.raw.game_over, 1)

    var sfxEnabled = true
    var sfxVolume = 1.0f

    fun playJump() {
        if (sfxEnabled) soundPool.play(jumpSound, sfxVolume, sfxVolume, 1, 0, 1f)
    }

    fun playFall() {
        if (sfxEnabled) soundPool.play(fallSound, sfxVolume, sfxVolume, 1, 0, 1f)
    }

    fun playGameOver() {
        if (sfxEnabled) soundPool.play(gameOverSound, sfxVolume, sfxVolume, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
