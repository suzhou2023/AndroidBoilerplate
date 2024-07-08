package com.bbt2000.boilerplate.interview.anim

import android.view.animation.Interpolator
import kotlin.math.sin

/**
 *  author : suzhou
 *  date : 2024/7/8 18:06
 *  description :
 */
class SinInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float {
        var input = input
        input -= 0.5f
        return sin((60 * input).toDouble()).toFloat() + 0.5f
    }
}


