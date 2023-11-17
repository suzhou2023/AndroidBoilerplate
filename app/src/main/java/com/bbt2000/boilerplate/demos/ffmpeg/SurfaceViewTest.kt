package com.bbt2000.boilerplate.demos.ffmpeg

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import com.bbt2000.boilerplate.demos.ffmpeg.jni.Jni
import com.bbt2000.gles.base.BaseSurfaceView
import com.bbt2000.gles.jni.JniGL
import com.orhanobut.logger.Logger

/**
 *  author : suzhou
 *  date : 2023/11/12
 *  description :
 */
class SurfaceViewTest(
    context: Context,
    attrs: AttributeSet? = null
) : BaseSurfaceView(context, attrs) {

    private var ffContext: Long = 0

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (ffContext > 0 && glContext > 0) {
                Jni.readOneFrame(ffContext, glContext)
                glHandler.post(this)
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        super.surfaceCreated(holder)
        ffContext = Jni.createFFContext()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        glHandler.post {
            // 创建opengl程序
            JniGL.nativeCreateProgram(glContext, "shader/v_simple_m_flip.glsl", "shader/f_yuv2rgb.glsl")
            // 打开流
            val ret = Jni.openRtspStream(ffContext, "rtsp://192.168.43.87:8554/stream")
            if (!ret) return@post
            // 设置opengl矩阵
            Jni.glSetMatrix(
                ffContext = ffContext,
                glContext = glContext,
                windowW = width,
                windowH = height,
                rotate = false,
            )
//            Jni.readFrames(ffContext, glContext)
            glHandler.post(runnable)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        glHandler.post {
            Jni.destroyFFContext(ffContext)
            ffContext = 0
            JniGL.nativeDestroyGLContext(glContext)
            glContext = 0
        }
        glHandler.looper.quitSafely()
    }
}




























