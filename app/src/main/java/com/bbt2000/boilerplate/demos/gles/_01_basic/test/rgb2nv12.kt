package com.bbt2000.boilerplate.demos.gles._01_basic.test

import androidx.core.graphics.drawable.toBitmap
import com.bbt2000.boilerplate.R
import com.bbt2000.boilerplate.common.util.ContextUtil
import com.bbt2000.boilerplate.common.util.FileUtil
import com.bbt2000.boilerplate.demos.gles._01_basic.jni.Jni
import com.bbt2000.boilerplate.demos.gles._01_basic.jni.Jni.nativeRgb2nv12
import com.bbt2000.gles.jni.JniGL
import com.orhanobut.logger.Logger
import java.io.File
import java.nio.ByteBuffer

/**
 *  author : suzhou
 *  date : 2023/11/14
 *  description :
 */
fun rgb2nv12(glContext: Long) {
    JniGL.createProgram(glContext, "shader/v_simple.glsl", "shader/f_rgb2nv12_y.glsl", 0)
    JniGL.createProgram(glContext, "shader/v_simple.glsl", "shader/f_rgb2nv12_uv.glsl", 1)
    val bitmap = ContextUtil.application.resources.getDrawable(R.drawable.profile_432x432).toBitmap()

    val begin = System.currentTimeMillis()
    nativeRgb2nv12(glContext, bitmap, object : Jni.IFrameCallback {
        override fun callback(byteBuffer: ByteBuffer, width: Int, height: Int) {
            val byteArray = ByteArray(byteBuffer.remaining())
            byteBuffer.get(byteArray, 0, byteArray.size)

            val file = File("${FileUtil.getExternalPicDir()}/profile_432x432_NV12")
            file.writeBytes(byteArray)

            val end = System.currentTimeMillis()
            Logger.d("Total time: ${end - begin}ms")
        }
    })
}