package com.acnteam.crashybird.presentation.screen.home

import androidx.annotation.FontRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.acnteam.crashybird.R
import com.acnteam.crashybird.presentation.ui.theme.OrangeAccent

@Preview
@Composable
fun Banner(
    text: String = "Banner",
    fontSize: Float = 160f,
    textColor: Color = OrangeAccent,
    strokeColor: Color = Color.Black,
    strokeWidth: Float = 10f,
    height: Float = 120f,
    @FontRes fontRes: Int = R.font.comic_neue_bold_italic,
) {
    val context = LocalContext.current
    val typeface = remember(fontRes) {
        ResourcesCompat.getFont(context, fontRes)
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp),
    ) {
        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = fontSize
            textAlign = android.graphics.Paint.Align.CENTER
            this.typeface = typeface
        }

        // Vẽ stroke
        paint.style = android.graphics.Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.color = strokeColor.toArgb()
        drawContext.canvas.nativeCanvas.drawText(
            text,
            size.width / 2,
            size.height / 2,
            paint
        )

        // Vẽ fill
        paint.style = android.graphics.Paint.Style.FILL
        paint.color = textColor.toArgb()
        drawContext.canvas.nativeCanvas.drawText(
            text,
            size.width / 2,
            size.height / 2,
            paint
        )
    }
}
