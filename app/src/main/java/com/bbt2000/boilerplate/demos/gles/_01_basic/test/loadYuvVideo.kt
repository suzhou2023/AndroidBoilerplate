package com.bbt2000.boilerplate.demos.gles._01_basic.test

import com.bbt2000.boilerplate.demos.gles._01_basic.jni.Jni
import com.bbt2000.gles.jni.JniGL

/**
 *  author : suzhou
 *  date : 2023/11/15
 *  description : 渲染yuv视频
 */
fun loadYuvVideo(glContext: Long) {
    JniGL.createProgram(glContext, "shader/v_simple_m.glsl", "shader/f_yuv2rgb.glsl")
    Jni.nativeLoadYuv2(glContext)
}