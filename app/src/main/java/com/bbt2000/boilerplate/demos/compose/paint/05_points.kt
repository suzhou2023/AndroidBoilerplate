package com.bbt2000.boilerplate.demos.compose.paint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val points = listOf<Offset>(
    Offset(60f, 60f),
    Offset(120f, 180f),
    Offset(240f, 240f),
    Offset(300f, 360f),
    Offset(480f, 420f),
    Offset(540f, 360f),
)

@Composable
fun MyPaint05() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawPoints(
            color = Color.Red,
            points = points,
            pointMode = PointMode.Points,
            strokeWidth = 10f,
            cap = StrokeCap.Round,
        )
    }
}






