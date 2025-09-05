package com.acnteam.crashybird.presentation.screen.play.component

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator


fun vibrateFor5s(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(5000)
    }
}

