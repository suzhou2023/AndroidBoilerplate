package com.bbt2000.boilerplate.interview.anim

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.bbt2000.boilerplate.R


class AnimActivity : AppCompatActivity() {
    lateinit var iv: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim)
        iv = findViewById(R.id.iv)
    }

    override fun onResume() {
        super.onResume()
        startSpringAnimation()
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
     * 属性动画ObjectAnimator使用
     */
    private fun startPropertyAnimator() {
        val animator = ObjectAnimator.ofFloat(iv, "translationX", 0f, 100f)
            .setDuration(3000)
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    /**
     * 属性动画AnimatorSet使用
     */
    private fun startPropertyAnimatorSet() {
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
     * 属性动画ValueAnimator使用
     */
    private fun startValueAnimator() {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.setDuration(300)
        valueAnimator.start()
        valueAnimator.addUpdateListener { animation ->
            // 动画更新过程中的动画值，可以根据动画值的变化来关联对象的属性，实现属性动画
            val value = animation.animatedValue as Float
            Log.d("ValueAnimator", "动画值：$value")
        }
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
        springForce.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY) // 阻尼比
        springForce.setStiffness(SpringForce.STIFFNESS_LOW) // 刚度
        springAnimation.setSpring(springForce)
        // 启动动画
        springAnimation.start()
    }


}