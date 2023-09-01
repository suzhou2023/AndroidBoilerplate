package com.bbt2000.boilerplate.demos.gles._01_basics

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.SurfaceHolder
import androidx.core.graphics.drawable.toBitmap
import com.bbt2000.boilerplate.R
import com.bbt2000.boilerplate.demos.gles.jni.Jni
import com.bbt2000.boilerplate.demos.gles.widget.AutoFitSurfaceView
import com.orhanobut.logger.Logger


/**
 *  author : suzhou
 *  date : 2023/7/16
 *  description :
 */
class SurfaceViewTest(context: Context, attrs: AttributeSet? = null) :
    AutoFitSurfaceView(context, attrs), SurfaceHolder.Callback {

    private var glContext: Long = 0
    private val handlerThread: HandlerThread by lazy { HandlerThread("gl-render").apply { start() } }
    private val handler: Handler = Handler(handlerThread.looper)


    init {
        holder.addCallback(this)
//        setAspectRatio(4, 3)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        handler.post {
            glContext = Jni.nativeCreateGLContext(assetManager = context.assets)
            if (glContext <= 0) return@post
            Jni.nativeEGLCreateSurface(glContext, holder.surface, 0)
            Jni.nativeLoadVertices(glContext)
        }
    }

    // opengl api练习
    fun apiTest() {
        nativeApiTest(glContext)
    }

    // 渲染图片
    fun texture() {
        Jni.nativeCreateProgram(glContext, "shader/v_simple.glsl", "shader/f_tex.glsl")
        val bitmap = context.resources.getDrawable(R.drawable.profile_432x431)
            .toBitmap(config = Bitmap.Config.ARGB_8888)
        Logger.d("byteCount=${bitmap.byteCount / 432 / 431}")
        nativeTexture(glContext, bitmap)
    }

    // 渲染yuv图片
    fun loadYuv() {
        Jni.nativeCreateProgram(glContext, "shader/v_simple.glsl", "shader/f_yuv2rgb.glsl")
        nativeLoadYuv(glContext)
    }

    // 渲染yuv视频
    fun loadYuvVideo() {
        Jni.nativeCreateProgram(glContext, "shader/v_simple_m.glsl", "shader/f_yuv2rgb.glsl")
        nativeLoadYuv2(glContext)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        handler.post {
            texture()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        handler.post {
            Jni.nativeDestroyGLContext(glContext)
        }
    }


    private external fun nativeApiTest(glContext: Long)
    private external fun nativeTexture(glContext: Long, bitmap: Bitmap)
    private external fun nativeLoadYuv(glContext: Long)
    private external fun nativeLoadYuv2(glContext: Long)


    companion object {
        const val TAG = "SurfaceViewTest"

        init {
            System.loadLibrary("gl_basics")
        }
    }
}


