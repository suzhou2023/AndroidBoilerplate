package com.bbt2000.boilerplate.demos.compose.paint

import android.graphics.Paint.Style
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MyPaint9() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawIntoCanvas { canvas ->
            adjustCoordinates(canvas, size)
            drawGrid(canvas, size)

            val paint = Paint().apply {
                color = Color.Blue
            }

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, 200f)
                lineTo(150f, 400f)
//                close()
            }
            canvas.drawPath(path, paint)

            val path2 = Path().apply {
                moveTo(0f, 0f)
                lineTo(150f, 200f)
                lineTo(300f, 0f)
                lineTo(300f, 200f)
                lineTo(150f, 400f)
//                close()
            }
            paint.apply {
                style = PaintingStyle.Stroke
                strokeWidth = 5f
            }
            canvas.drawPath(path2, paint)

        }
    }
}






