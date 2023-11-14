package com.bbt2000.boilerplate.demos.gles._01_basic.test

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.bbt2000.boilerplate.R
import com.bbt2000.boilerplate.common.util.ContextUtil
import com.bbt2000.boilerplate.demos.gles._01_basic.jni.Jni
import com.bbt2000.gles.jni.JniGL

/**
 *  author : suzhou
 *  date : 2023/11/15
 *  description : 渲染图片
 */
fun texture(glContext: Long) {
    JniGL.nativeCreateProgram(glContext, "shader/v_simple.glsl", "shader/f_tex_flip.glsl")
    // 注意：android系统有可能会对图片进行缩放
    val bitmap = ContextUtil.application.resources.getDrawable(R.drawable.profile_432x432)
        .toBitmap(config = Bitmap.Config.ARGB_8888)

    Jni.nativeTexture(glContext, bitmap)
}




















