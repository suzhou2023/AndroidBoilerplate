package com.bbt2000.boilerplate.interview.anim

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.contains
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.bbt2000.boilerplate.R
import com.orhanobut.logger.Logger


class PropertyAnimActivity : AppCompatActivity() {
    lateinit var container: LinearLayout
    lateinit var iv: ImageView
    lateinit var iv2: ImageView
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim)
        container = findViewById(R.id.container)
        iv = findViewById(R.id.iv)
        iv2 = findViewById(R.id.iv2)
        button = findViewById(R.id.button)


        // LayoutTransition动画：
        // 使用系统默认的LayoutTransition动画，需要在xml的ViewGroup中配置animateLayoutChanges
//        configLayoutTransition(container)
        button.setOnClickListener {
            if (container.contains(iv)) {
                container.removeView(iv)
            } else {
                container.addView(iv, 0)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startViewPropertyAnimator()
    }

    /**
     * 视图动画（View动画）包括平移、旋转、缩放、透明度四种
     * 可以定义在xml文件中，通过AnimationUtils加载，然后调用控件的startAnimation即可播放
     */
    private fun startViewAnim() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.view_anim_set)
        iv.startAnimation(anim)
    }

    /**
     * 属性动画API：ObjectAnimator
     */
    private fun startObjectAnimator() {
        val animator = ObjectAnimator.ofFloat(iv, "rotationY", 0f, 360f)
            .setDuration(1000)
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    /**
     * 属性动画API：ObjectAnimator + PropertyValuesHolder
     */
    private fun startObjectAnimator2() {
        val holder1 = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.5f)
        val holder2 = PropertyValuesHolder.ofFloat("rotationY", 0f, 360f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(iv, holder1, holder2).setDuration(3000)
        animator.start()
    }

    /**
     * 属性动画API：AnimatorSet使用
     */
    private fun startAnimatorSet() {
        val scaleX = ObjectAnimator.ofFloat(iv, "scaleX", 1f, 0.5f)
            .setDuration(2000)
        val scaleY = ObjectAnimator.ofFloat(iv, "scaleY", 1f, 0.5f)
            .setDuration(2000)
        val rotX = ObjectAnimator.ofFloat(iv, "rotationX", 0f, 360f)
            .setDuration(3000)
        val rotY = ObjectAnimator.ofFloat(iv, "rotationY", 0f, 360f)
            .setDuration(3000)

        val set = AnimatorSet()
        set.play(scaleX).with(scaleY).with(rotX).with(rotY)
        set.start()
    }

    /**
     * 属性动画也可以定义在xml中，使用AnimatorInflater加载
     * 然后调用Animator.setTarget(View)和Animator.start()进行播放
     */
    private fun startXmlPropertyAnimator() {
        val animator =
            AnimatorInflater.loadAnimator(applicationContext, R.animator.property_animator_set)
        animator.interpolator = BounceInterpolator()
        animator.setTarget(iv)
        animator.start()
    }

    /**
     * 属性动画API：ValueAnimator（基本数值类型）
     * IntEvaluator
     * FloatEvaluator
     * ArgbEvaluator
     */
    private fun startValueAnimator() {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 300
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.start()
        valueAnimator.addUpdateListener { animation ->
            // 动画更新过程中的动画值，可以根据动画值的变化来关联对象的属性，实现属性动画
            val value = animation.animatedValue as Float
            Log.d("ValueAnimator", "动画值：$value")
        }
    }

    /**
     * 属性动画API：ValueAnimator（自定义类型对象）
     * 需要重写估值器接口：TypeEvaluator<T>
     */
    private fun startValueAnimator2() {
        val valueAnimator = ValueAnimator.ofObject(object : TypeEvaluator<Long> {
            override fun evaluate(fraction: Float, startValue: Long, endValue: Long): Long {
                val startA = startValue shr 24 and 0xff
                val startR = startValue shr 16 and 0xff
                val startG = startValue shr 8 and 0xff
                val startB = startValue and 0xff

                val endA = endValue shr 24 and 0xff
                val endR = endValue shr 16 and 0xff
                val endG = endValue shr 8 and 0xff
                val endB = endValue and 0xff

                return (startA + (fraction * (endA - startA)).toInt() shl 24) or
                        (startR + (fraction * (endR - startR)).toInt() shl 16) or
                        (startG + (fraction * (endG - startG)).toInt() shl 8) or
                        (startB + (fraction * (endB - startB)).toInt())
            }
        }, 0xffff0000, 0xff00ff00)
        valueAnimator.duration = 1000
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.start()
        valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val value = animation.animatedValue as Long
                Logger.d("0x${value.toString(16)}")
            }
        })
    }

    /**
     * 属性动画API：ViewPropertyAnimator
     */
    private fun startViewPropertyAnimator() {
        iv.animate()
            .scaleX(0.5f)
            .rotationY(360f)
            .setDuration(2000)
            .start()
    }

    /**
     * 物理动画SpringAnimation使用
     * 物理动画也是基于属性动画
     */
    private fun startSpringAnimation() {
        val springAnimation =
            SpringAnimation(iv, DynamicAnimation.TRANSLATION_X, 400f)
        // 配置SpringForce
        val springForce = SpringForce(400f)
        springForce.dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY // 阻尼比
        springForce.stiffness = SpringForce.STIFFNESS_LOW // 刚度
        springAnimation.spring = springForce
        // 启动动画
        springAnimation.start()
    }


}