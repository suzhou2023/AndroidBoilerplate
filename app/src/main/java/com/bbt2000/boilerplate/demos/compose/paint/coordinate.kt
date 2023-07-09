package com.bbt2000.boilerplate.demos.compose.paint

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle

fun adjustCoordinates(canvas: Canvas, size: Size) {
    canvas.scale(1f, -1f)
    canvas.translate(0f, -size.height)
}

fun drawGrid(canvas: Canvas, size: Size, step: Float = 50f) {
    val paint = Paint()
    paint.style = PaintingStyle.Fill
    paint.color = Color.Gray

    canvas.save()
    var translateY = 0f
    while (translateY < size.height) {
        canvas.drawLine(
            p1 = Offset(0f, 0f),
            p2 = Offset(size.width, 0f),
            paint = paint
        )
        canvas.translate(0f, step)
        translateY += step
    }
    canvas.restore()

    canvas.save()
    var translateX = 0f
    while (translateX < size.width) {
        canvas.drawLine(
            p1 = Offset(0f, 0f),
            p2 = Offset(0f, size.height),
            paint = paint,
        )
        canvas.translate(step, 0f)
        translateX += step
    }
    canvas.restore()
}