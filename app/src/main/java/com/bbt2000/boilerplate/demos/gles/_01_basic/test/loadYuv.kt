package com.bbt2000.boilerplate.demos.gles._01_basic.test

import com.bbt2000.boilerplate.demos.gles._01_basic.jni.Jni
import com.bbt2000.gles.jni.JniGL

/**
 *  author : suzhou
 *  date : 2023/11/15
 *  description : 渲染yuv图片
 */
fun loadYuv(glContext: Long) {
    JniGL.nativeCreateProgram(glContext, "shader/v_simple.glsl", "shader/f_yuv2rgb.glsl")
    Jni.nativeLoadYuv(glContext)
}