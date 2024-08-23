package com.bbt2000.boilerplate.interview.customview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

/**
 *  author : suzhou
 *  date : 2024/8/22
 *  description :
 */
fun drawCoordinates(canvas: Canvas, paint: Paint, gridPath: Path, axisPath: Path) {
    // 网格
    paint.color = Color.GRAY
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 0.5f
    canvas.drawPath(gridPath, paint)

    // 坐标轴
    paint.strokeWidth = 1.5f
    canvas.drawPath(axisPath, paint)
}