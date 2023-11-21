package com.bbt2000.boilerplate.demos.gles._03_3d.test

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.bbt2000.boilerplate.R
import com.bbt2000.boilerplate.common.util.ContextUtil
import com.bbt2000.boilerplate.demos.gles._03_3d.jni.Jni
import com.bbt2000.gles.jni.JniGL

/**
 *  author : suzhou
 *  date : 2023/11/21
 *  description :
 */
fun tex3d(glContext: Long) {
    JniGL.createProgram(glContext, "shader/v_3d.glsl", "shader/f_tex.glsl")
    val bitmap = ContextUtil.application.resources.getDrawable(R.drawable.profile_432x432)
        .toBitmap(config = Bitmap.Config.ARGB_8888)
    Jni.tex3d(glContext, bitmap)
}