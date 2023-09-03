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
import com.bbt2000.boilerplate.util.FileUtil
import com.orhanobut.logger.Logger
import java.io.File
import java.nio.ByteBuffer


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

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        handler.post {
            loadYuv()
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
    private external fun nativeRgb2vyuy(glContext: Long, bitmap: Bitmap, callback: IFrameCallback)
    private external fun nativeRgb2nv12(glContext: Long, bitmap: Bitmap, callback: IFrameCallback)


    // rgb转nv12
    fun rgb2nv12() {
        Jni.nativeCreateProgram(glContext, "shader/v_simple.glsl", "shader/f_rgb2nv12_y.glsl", 0)
        Jni.nativeCreateProgram(glContext, "shader/v_simple.glsl", "shader/f_rgb2nv12_uv.glsl", 1)
        val bitmap = context.resources.getDrawable(R.drawable.profile_432x432).toBitmap()

        val begin = System.currentTimeMillis()
        nativeRgb2nv12(glContext, bitmap, object : IFrameCallback {
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

    // rgb转vyuy
    fun rgb2vyuy() {
        Jni.nativeCreateProgram(glContext, "shader/v_simple.glsl", "shader/f_rgb2vyuy.glsl")
        val bitmap = context.resources.getDrawable(R.drawable.wy_300x200).toBitmap()

        val begin = System.currentTimeMillis()
        nativeRgb2vyuy(glContext, bitmap, object : IFrameCallback {
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

    // 渲染图片
    fun texture() {
        Jni.nativeCreateProgram(glContext, "shader/v_simple.glsl", "shader/f_tex_flip.glsl")
        // 注意：android系统有可能会对图片进行缩放
        val bitmap = context.resources.getDrawable(R.drawable.profile_432x432)
            .toBitmap(config = Bitmap.Config.ARGB_8888)

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

    // opengl api练习
    fun apiTest() {
        nativeApiTest(glContext)
    }


    // native图像帧数据回调
    interface IFrameCallback {
        fun callback(byteBuffer: ByteBuffer, width: Int, height: Int)
    }

    companion object {
        const val TAG = "SurfaceViewTest"

        init {
            System.loadLibrary("gl_basics")
        }
    }
}


















