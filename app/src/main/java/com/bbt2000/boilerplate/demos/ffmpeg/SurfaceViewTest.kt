package com.bbt2000.boilerplate.demos.ffmpeg

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import com.bbt2000.boilerplate.demos.ffmpeg.jni.Jni
import com.bbt2000.gles.base.BaseSurfaceView
import com.bbt2000.gles.jni.JniGL

/**
 *  author : suzhou
 *  date : 2023/11/12
 *  description :
 */
class SurfaceViewTest(
    context: Context,
    attrs: AttributeSet? = null
) : BaseSurfaceView(context, attrs) {

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        glHandler.post {
            JniGL.nativeCreateProgram(glContext, "shader/v_simple_m_flip.glsl", "shader/f_yuv2rgb.glsl")
            Jni.openStream(glContext, "rtsp://192.168.43.87:8554/stream")
        }
    }
}




























