package com.acnteam.crashybird.presentation.widget.mapplayer

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import com.acnteam.crashybird.R
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.presentation.MainActivity

private val LEVEL_KEY = intPreferencesKey("current_level")

object MapPlayerWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val state = currentState(LEVEL_KEY) ?: 1
            val level = Level.getByLevelNumber(state)
            MapPlayer(level = level)
        }
    }

    suspend fun updateLevel(context: Context, newLevelNumber: Int) {
        val glanceIds = androidx.glance.appwidget.GlanceAppWidgetManager(context)
            .getGlanceIds(MapPlayerWidget::class.java)

        glanceIds.forEach { id ->
            updateAppWidgetState(context, id) { prefs ->
                prefs[LEVEL_KEY] = newLevelNumber
            }
        }

        updateAll(context)
    }
}

@Composable
fun MapPlayer(
    level: Level = Level.One,
) {

    Box(
        modifier = GlanceModifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            provider = ImageProvider(level.image),
            contentDescription = "Map",
            contentScale = ContentScale.FillBounds,
            modifier = GlanceModifier.fillMaxSize()
        )

        Image(
            ImageProvider(R.drawable.play),
            contentDescription = "Play button",
            modifier = GlanceModifier.size(40.dp)
                .clickable(actionStartActivity<MainActivity>())
        )
    }
}

class MapPlayerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = MapPlayerWidget
}
