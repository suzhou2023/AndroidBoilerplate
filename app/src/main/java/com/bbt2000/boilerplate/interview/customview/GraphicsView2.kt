package com.bbt2000.boilerplate.interview.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.animation.LinearInterpolator

/**
 *  author : suzhou
 *  date : 2024/8/25
 *  description :
 */
class GraphicsView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseCustomView(context, attrs, defStyleAttr) {
    private val gPath = Path() // 图形
    private val pathMeasure = PathMeasure()
    private var pathLength = 0f // 路径长度
    private var currentDistance1 = 0f // 动画当前点的路径长度
    private var currentDistance2 = 0f // 动画当前点的路径长度
    private var currentPosition1 = FloatArray(2) // 动画当前点位置
    private var currentTangent1 = FloatArray(2) // 动画当前点切线方向
    private var currentPosition2 = FloatArray(2) // 动画当前点位置
    private var currentTangent2 = FloatArray(2) // 动画当前点切线方向
    lateinit var bitmap: Bitmap
    lateinit var bCanvas: Canvas


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // bitmap canvas
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bCanvas = Canvas(bitmap)

        // circle path
        gPath.addCircle(0f, 0f, width / 2f - 20f, Path.Direction.CW)
        pathMeasure.setPath(gPath, false)
        pathLength = pathMeasure.length
        startAnimation()
    }


    private fun startAnimation() {
        val animator = ValueAnimator.ofFloat(0f, pathLength)
        animator.duration = 3000
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener { animation ->
            currentDistance1 = animation.animatedValue as Float
            // 获取Path上当前距离的点和切线方向
            pathMeasure.getPosTan(currentDistance1, currentPosition1, currentTangent1)
//            invalidate()
        }
        animator.start()

        val animator2 = ValueAnimator.ofFloat(0f, pathLength)
        animator2.duration = (3000 * Math.E).toLong()
        animator2.interpolator = LinearInterpolator()
        animator2.repeatCount = ValueAnimator.INFINITE
        animator2.addUpdateListener { animation ->
            currentDistance2 = animation.animatedValue as Float
            // 获取Path上当前距离的点和切线方向
            pathMeasure.getPosTan(currentDistance2, currentPosition2, currentTangent2)
            invalidate()
        }
        animator2.start()
    }


    override fun onDraw(canvas: Canvas) {
        canvas.translate(width / 2f, height / 2f)
        canvas.scale(1f, -1f)
        drawCoordinates(canvas, paint, gridPath, axisPath)

        canvas.scale(1f, -1f)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        // 子路径
        paint.color = Color.BLUE
        paint.strokeWidth = 3f
        paint.style = Paint.Style.STROKE
        canvas.drawPath(gPath, paint)


        paint.color = Color.BLUE
        paint.style = Paint.Style.FILL
        canvas.drawCircle(currentPosition1[0], currentPosition1[1], 6f, paint)
        canvas.drawCircle(currentPosition2[0], currentPosition2[1], 6f, paint)

        paint.style = Paint.Style.STROKE
        bCanvas.translate(width / 2f, height / 2f)
        bCanvas.scale(1f, -1f)
        bCanvas.drawLine(currentPosition1[0], currentPosition1[1], currentPosition2[0], currentPosition2[1], paint)
        canvas.drawLine(currentPosition1[0], currentPosition1[1], currentPosition2[0], currentPosition2[1], paint)
    }
}