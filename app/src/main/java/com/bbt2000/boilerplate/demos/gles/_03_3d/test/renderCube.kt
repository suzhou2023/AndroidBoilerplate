package com.bbt2000.boilerplate.demos.gles._03_3d.test

import com.bbt2000.boilerplate.demos.gles._03_3d.jni.Jni.renderCube
import com.bbt2000.gles.jni.JniGL

/**
 *  author : suzhou
 *  date : 2023/11/21
 *  description :
 */
fun renderCube(glContext: Long) {
    JniGL.createProgram(glContext, "shader/v_3d_color.glsl", "shader/f_color.glsl")
    renderCube(glContext)
}