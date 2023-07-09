package com.bbt2000.boilerplate.demos.compose.paint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas


@Composable
fun MyPaint06() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawIntoCanvas { canvas ->
            adjustCoordinates(canvas, size)
            drawGrid(canvas, size)

            val paint = Paint().apply {
                color = Color.Red
                strokeWidth = 5f
            }
            canvas.drawRect(
                Rect(50f, 50f, 50f + 300f, 50f + 300f),
                paint
            )
        }
    }
}






