package com.bbt2000.boilerplate.demos.compose.paint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview(showBackground = true, widthDp = 160, heightDp = 320)
@Composable
fun MyPaint01() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        drawCircle(
            center = Offset(canvasWidth / 2, 50.dp.toPx()),
            radius = 40.dp.toPx(),
            color = Color.Blue,
            style = Fill,
        )
        drawCircle(
            center = Offset(canvasWidth / 2, 150.dp.toPx()),
            radius = 40.dp.toPx(),
            color = Color.Red,
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}






