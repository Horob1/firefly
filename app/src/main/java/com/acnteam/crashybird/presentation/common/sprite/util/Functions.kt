package com.acnteam.crashybird.presentation.common.sprite.util

import com.acnteam.crashybird.presentation.common.sprite.domain.ScreenCategory

internal fun Float.parseCategory(): ScreenCategory {
    return when (this) {
        in 0f..360f -> ScreenCategory.Small
        in 360f..600f -> ScreenCategory.Normal
        in 600f..800f -> ScreenCategory.Large
        else -> ScreenCategory.Tablet
    }
}