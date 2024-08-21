package com.bbt2000.boilerplate.interview.anim

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.ViewGroup

/**
 *  author : suzhou
 *  date : 2024/8/21
 *  description : 属性动画API：LayoutTransition
 */
@SuppressLint("ObjectAnimatorBinding")
fun configLayoutTransition(container: ViewGroup) {
    val layoutTransition = LayoutTransition()
    container.layoutTransition = layoutTransition
    // 自定义 APPEARING 动画
    val appearingAnimator = ObjectAnimator.ofFloat(null, "alpha", 0f, 1f)
    layoutTransition.setAnimator(LayoutTransition.APPEARING, appearingAnimator)
    // 自定义 DISAPPEARING 动画
    val disappearingAnimator = ObjectAnimator.ofFloat(null, "scaleX", 1f, 0f)
    layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, disappearingAnimator)
    // 设置动画持续时间
    layoutTransition.setDuration(LayoutTransition.APPEARING, 2000)
    layoutTransition.setDuration(LayoutTransition.DISAPPEARING, 2000)
}