package com.bbt2000.boilerplate.interview.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View


/**
 *  author : suzhou
 *  date : 2024/8/22
 *  description :
 */
open class BaseCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    val paint = Paint()
    var step = 40f
    val gridPath = Path() // 网格
    val axisPath = Path() // 坐标轴
    val gPath = Path() // 图形
    val gPathPartial = Path() // 部分图形
    val pathMeasure = PathMeasure()
    var pathLength = 0f // 路径长度
    var currentDistance = 0f // 动画当前点的路径长度
    var currentPosition = FloatArray(2) // 动画当前点位置
    var currentTangent = FloatArray(2) // 动画当前点切线方向
    val points = mutableListOf<PointF>() // 图形点集
    val pointsFloat = mutableListOf<Float>() // 图形点集float

    init {
        paint.isAntiAlias = true
        paint.isDither = true
    }

    fun startAnimation() {
        val animator = ValueAnimator.ofFloat(0f, pathLength)
        animator.duration = 3000
        animator.addUpdateListener { animation ->
            currentDistance = animation.animatedValue as Float
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
        // 网格路径
        for (i in step.toInt()..(height / 2f).toInt() step step.toInt()) {
            gridPath.moveTo(-width / 2f, i.toFloat())
            gridPath.lineTo(width / 2f, i.toFloat())
        }
        for (i in -step.toInt() downTo -(height / 2f).toInt() step step.toInt()) {
            gridPath.moveTo(-width / 2f, i.toFloat())
            gridPath.lineTo(width / 2f, i.toFloat())
        }
        for (i in step.toInt()..(width / 2f).toInt() step step.toInt()) {
            gridPath.moveTo(i.toFloat(), -height / 2f)
            gridPath.lineTo(i.toFloat(), height / 2f)
        }
        for (i in -step.toInt() downTo -(width / 2f).toInt() step step.toInt()) {
            gridPath.moveTo(i.toFloat(), -height / 2f)
            gridPath.lineTo(i.toFloat(), height / 2f)
        }

        // 坐标轴路径
        axisPath.moveTo(-width / 2f, 0f)
        axisPath.lineTo(width / 2f, 0f)
        axisPath.rLineTo(-10 * 1.73f, 10f)
        axisPath.moveTo(width / 2f, 0f)
        axisPath.rLineTo(-10 * 1.73f, -10f)
        axisPath.moveTo(0f, -height / 2f)
        axisPath.lineTo(0f, height / 2f)
        axisPath.rLineTo(-10f, -10 * 1.73f)
        axisPath.lineTo(0f, height / 2f)
        axisPath.rLineTo(10f, -10 * 1.73f)
    }
}