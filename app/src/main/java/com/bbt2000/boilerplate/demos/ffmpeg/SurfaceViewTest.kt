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

    // 打开流
    private val openStream: Runnable = object : Runnable {
        override fun run() {
            if (ffContext > 0 && glContext > 0) {
                // 打开流
                val success = Jni.openRtspStream(ffContext, "rtsp://192.168.43.87:8554/stream")
                if (success) {
                    // 设置opengl矩阵
                    Jni.glConfigMatrix(
                        ffContext = ffContext,
                        glContext = glContext,
                        windowW = width,
                        windowH = height,
                        scaleType = 2,
                        rotate = false
                    )
                    glHandler.post(readFrame)
                } else {
                    postDelayed(this, 3000)
                }
            }
        }
    }

    // 读取帧
    private val readFrame: Runnable = object : Runnable {
        override fun run() {
            if (ffContext > 0 && glContext > 0) {
                val ret = Jni.readOneFrame(ffContext, glContext)
                if (ret != -541478725) { // EOF
                    glHandler.post(this)
                } else {
                    glHandler.post(openStream)
                }
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
            JniGL.createProgram(glContext, "shader/v_simple_m_flip.glsl", "shader/f_yuv2rgb.glsl")

            // 打开流
            glHandler.post(openStream)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        glHandler.post {
            Jni.destroyFFContext(ffContext)
            ffContext = 0
            JniGL.destroyGLContext(glContext)
            glContext = 0
        }
        glHandler.looper.quitSafely()
    }
}




























