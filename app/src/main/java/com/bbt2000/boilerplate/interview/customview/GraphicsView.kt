package com.bbt2000.boilerplate.interview.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.util.AttributeSet

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
    private var xRange = floatArrayOf(-5f, 5f) // 函数x坐标范围
    private var margin = 0.5f // x坐标范围两端到控件边界的余量
    private var scaleRatio = 1f // 绘制函数路径的画布缩放比例
    private val gPath = Path() // 图形
    private val gPathPartial = Path() // 部分图形
    private val pathMeasure = PathMeasure()
    private var pathLength = 0f // 路径长度
    private var currentDistance = 0f // 动画当前点的路径长度
    private var currentPosition = FloatArray(2) // 动画当前点位置
    private var currentTangent = FloatArray(2) // 动画当前点切线方向
    private val points = mutableListOf<PointF>() // 图形点集
    private val pointsFloat = mutableListOf<Float>() // 图形点集float
    private var drawOriginalPoints = false // 是否绘制原始函数点
    private var drawFinished = false // 绘制结束

    // 设置要绘制的函数和x坐标范围、取点间隔
    fun setFunc(
        func: (Double) -> Double,
        xMin: Float,
        xMax: Float,
        interval: Float,
        margin: Float,
        drawOriginalPoints: Boolean = false
    ) {
        xRange = floatArrayOf(xMin, xMax)
        this.margin = margin
        this.drawOriginalPoints = drawOriginalPoints

        var x = xRange[0]
        while (x <= xRange[1]) {
            points.add(PointF(x, func(x.toDouble()).toFloat()))
            pointsFloat.add(x)
            pointsFloat.add(func(x.toDouble()).toFloat())
            x += interval
        }
        // 二次贝塞尔曲线拟合到该点
        repeat(2) {
            points.add(PointF(xRange[1], func(xRange[1].toDouble()).toFloat()))
            pointsFloat.add(xRange[1])
            pointsFloat.add(func(xRange[1].toDouble()).toFloat())
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

    private fun startAnimation() {
        val animator = ValueAnimator.ofFloat(0f, pathLength)
        animator.duration = 3000
        animator.addUpdateListener { animation ->
            currentDistance = animation.animatedValue as Float
            if (currentDistance == pathLength) drawFinished = true
            // 获取Path上当前距离的点和切线方向
            pathMeasure.getPosTan(currentDistance, currentPosition, currentTangent)
            // 获取子路径
            pathMeasure.getSegment(0f, currentDistance, gPathPartial, true)
            invalidate()
        }
        animator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scaleRatio = width / (xRange[1] - xRange[0] + margin * 2)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.translate(width / 2f, height / 2f)
        canvas.scale(1f, -1f)
        drawCoordinates(canvas, paint, gridPath, axisPath)
        if (points.size == 0) return

        canvas.scale(scaleRatio, scaleRatio)

        // 原始函数点
        if (drawOriginalPoints) {
            paint.color = Color.RED
            paint.strokeWidth = 10f / scaleRatio
            paint.strokeCap = Paint.Cap.ROUND
            canvas.drawPoints(pointsFloat.toFloatArray(), paint)
        }

        // 子路径
        paint.color = Color.BLUE
        paint.strokeWidth = 3f / scaleRatio
        paint.style = Paint.Style.STROKE
        canvas.drawPath(gPathPartial, paint)

        // 在路径上绘制一个小圆点
        if (!drawFinished) {
            paint.color = Color.BLUE
            paint.style = Paint.Style.FILL
            canvas.drawCircle(currentPosition[0], currentPosition[1], 6f / scaleRatio, paint)
        }
    }
}