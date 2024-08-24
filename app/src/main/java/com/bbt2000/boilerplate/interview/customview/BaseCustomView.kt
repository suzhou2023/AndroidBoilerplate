package com.bbt2000.boilerplate.interview.customview

import android.content.Context
import android.graphics.Paint
import android.graphics.Path
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
    var step = 40f // 网格步长
    val gridPath = Path() // 网格
    val axisPath = Path() // 坐标轴

    init {
        paint.isAntiAlias = true
        paint.isDither = true
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