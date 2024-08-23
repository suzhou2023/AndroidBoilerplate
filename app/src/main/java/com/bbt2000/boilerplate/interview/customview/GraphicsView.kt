package com.bbt2000.boilerplate.interview.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import kotlin.math.sin

/**
 *  author : suzhou
 *  date : 2024/8/23
 *  description :
 */
class GraphicsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseCustomView(context, attrs, defStyleAttr) {
    init {
        var x = -5f
        while (x <= 5f) {
            points.add(PointF(x, sin(x.toDouble()).toFloat()))
            pointsFloat.add(x)
            pointsFloat.add(sin(x.toDouble()).toFloat())
            x += 0.2f
        }

        gPath.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size - 1) {
            val endX = (points[i].x + points[i + 1].x) / 2
            val endY = (points[i].y + points[i + 1].y) / 2
            gPath.quadTo(points[i].x, points[i].y, endX, endY)
        }
        pathMeasure.setPath(gPath, false)
        pathLength = pathMeasure.length

        // 启动动画
        startAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.translate(width / 2f, height / 2f)
        canvas.scale(1f, -1f)
        drawCoordinates(canvas, paint, gridPath, axisPath)

        canvas.scale(100f, 100f)

        // 原始函数点
//        paint.color = Color.RED
//        paint.strokeWidth = 0.08f
//        canvas.drawPoints(pointsFloat.toFloatArray(), paint)

        // 子路径
        paint.color = Color.BLUE
        paint.strokeWidth = 0.03f
        paint.style = Paint.Style.STROKE
        canvas.drawPath(gPathPartial, paint)

        // 在路径上绘制一个小圆点
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        canvas.drawCircle(currentPosition[0], currentPosition[1], 0.1f, paint)
    }
}