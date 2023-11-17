package com.bbt2000.boilerplate.demos.gles._01_basic.test

import androidx.core.graphics.drawable.toBitmap
import com.bbt2000.boilerplate.R
import com.bbt2000.boilerplate.common.util.ContextUtil
import com.bbt2000.boilerplate.common.util.FileUtil
import com.bbt2000.boilerplate.demos.gles._01_basic.jni.Jni
import com.bbt2000.gles.jni.JniGL
import com.orhanobut.logger.Logger
import java.io.File
import java.nio.ByteBuffer

/**
 *  author : suzhou
 *  date : 2023/11/14
 *  description :
 */
fun rgb2vyuy(glContext: Long) {
    JniGL.createProgram(glContext, "shader/v_simple.glsl", "shader/f_rgb2vyuy.glsl")
    val bitmap = ContextUtil.application.resources.getDrawable(R.drawable.wy_300x200).toBitmap()

    val begin = System.currentTimeMillis()
    Jni.nativeRgb2vyuy(glContext, bitmap, object : Jni.IFrameCallback {
        override fun callback(byteBuffer: ByteBuffer, width: Int, height: Int) {
            val byteArray = ByteArray(byteBuffer.remaining())
            byteBuffer.get(byteArray, 0, byteArray.size)

            val file = File("${FileUtil.getExternalPicDir()}/wy_300x200_VYUY")
            file.writeBytes(byteArray)

            val end = System.currentTimeMillis()
            Logger.d("Total time: ${end - begin}ms")
        }
    })
}