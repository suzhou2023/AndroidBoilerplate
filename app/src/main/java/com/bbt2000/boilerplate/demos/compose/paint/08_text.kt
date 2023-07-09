package com.bbt2000.boilerplate.demos.compose.paint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MyPaint8() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawIntoCanvas { canvas ->
            drawGrid(canvas, size)

            val paint = Paint().asFrameworkPaint().apply {
                textSize = 28.sp.toPx()
                color = android.graphics.Color.RED
            }


            val top = paint.fontMetrics.top
            val bottom = paint.fontMetrics.bottom
            println("top = $top")
            println("bottom = $bottom")

            canvas.nativeCanvas.drawText(
                "Hello Compose!",
                0f,
                -top,
                paint
            )

            paint.style = android.graphics.Paint.Style.STROKE
            paint.strokeWidth = 1.dp.toPx()
            paint.textAlign = android.graphics.Paint.Align.CENTER
            canvas.nativeCanvas.drawText(
                "Hello Compose!",
                size.width / 2,
                (size.height / 2),
                paint
            )
        }
    }
}






