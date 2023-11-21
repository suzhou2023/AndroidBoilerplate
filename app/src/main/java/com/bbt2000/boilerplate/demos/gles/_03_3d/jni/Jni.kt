package com.bbt2000.boilerplate.demos.gles._03_3d.jni

import android.graphics.Bitmap

/**
 *  author : suzhou
 *  date : 2023/11/21
 *  description :
 */
object Jni {
    init {
        System.loadLibrary("gl-3d")
    }

    external fun tex3d(glContext: Long, bitmap: Bitmap)

    external fun renderCube(glContext: Long)
}





























