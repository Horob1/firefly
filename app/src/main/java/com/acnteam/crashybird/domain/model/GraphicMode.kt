package com.acnteam.crashybird.domain.model

import androidx.annotation.StringRes
import com.acnteam.crashybird.R

sealed class GraphicMode(val fps: Int, @StringRes val name: Int) {
    companion object {
        fun getByFps(fps: Int): GraphicMode {
            return when (fps) {
                30 -> LOW
                60 -> MEDIUM
                90 -> HIGH
                120 -> ULTRA
                else -> LOW
            }
        }

        fun getAllGraphicMode(): List<GraphicMode> {
            return listOf(LOW, MEDIUM, HIGH, ULTRA)
        }
    }

    object LOW : GraphicMode(30, R.string.graphic_mode_low)
    object MEDIUM : GraphicMode(60, R.string.graphic_mode_medium)
    object HIGH : GraphicMode(90, R.string.graphic_mode_high)
    object ULTRA : GraphicMode(120, R.string.graphic_mode_ultra)
}
