package com.bbt2000.boilerplate.demos.gles._01_basics.jni

import android.content.res.AssetManager

/**
 *  author : suzhou
 *  date : 2023/8/19
 *  description :
 */
object Jni {
    init {
        System.loadLibrary("gl_basics")
    }

    external fun nativePrintGLSL(assetManager: AssetManager)
}