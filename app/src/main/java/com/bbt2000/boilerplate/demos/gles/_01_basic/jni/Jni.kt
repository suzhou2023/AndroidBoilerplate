package com.bbt2000.boilerplate.demos.gles._01_basic.jni

import android.graphics.Bitmap
import java.nio.ByteBuffer

/**
 *  author : suzhou
 *  date : 2023/11/14
 *  description :
 */
object Jni {
    init {
        System.loadLibrary("gl-basic")
    }

    external fun nativeApiTest(glContext: Long)
    external fun nativeTexture(glContext: Long, bitmap: Bitmap)
    external fun nativeLoadYuv(glContext: Long)
    external fun nativeLoadYuv2(glContext: Long)
    external fun nativeRgb2nv12(glContext: Long, bitmap: Bitmap, callback: IFrameCallback)
    external fun nativeRgb2vyuy(glContext: Long, bitmap: Bitmap, callback: IFrameCallback)


    // native图像帧数据回调
    interface IFrameCallback {
        fun callback(byteBuffer: ByteBuffer, width: Int, height: Int)
    }
}