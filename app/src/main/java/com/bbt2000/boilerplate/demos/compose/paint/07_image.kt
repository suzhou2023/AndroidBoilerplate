package com.bbt2000.boilerplate.demos.compose.paint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.bbt2000.boilerplate.R


@Composable
fun MyPaint7() {
    val bitmap = ImageBitmap.imageResource(id = R.drawable.wy_300x200)
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawIntoCanvas { canvas ->
            drawGrid(canvas, size)

            val paint = Paint()
            canvas.drawImage(bitmap, Offset(0f, 0f), paint)
            canvas.drawImageRect(
                image = bitmap,
                srcOffset = IntOffset(100, 0),
                srcSize = IntSize(125, 200),
                dstOffset = IntOffset(350, 0),
                dstSize = IntSize(125, 200),
                paint = paint
            )
            canvas.drawImageRect(
                image = bitmap,
                srcOffset = IntOffset(100, 0),
                srcSize = IntSize(100, 100),
                dstOffset = IntOffset(100, 300),
                dstSize = IntSize(800, 800),
                paint = paint
            )
        }
    }
}






