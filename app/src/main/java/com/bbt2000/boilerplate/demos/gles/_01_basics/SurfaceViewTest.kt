package com.bbt2000.boilerplate.demos.gles._01_basics

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import com.bbt2000.boilerplate.demos.gles.jni.Jni
import com.bbt2000.boilerplate.demos.gles.widget.AutoFitSurfaceView


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
//            nativeApiTest(glContext)

//            CoroutineScope(Dispatchers.Default).launch {
//                withContext(Dispatchers.IO) {
//                    nativeLoadYuv(holder.surface, context.assets)
//                }
//            }

//            val bitmap = context.resources.getDrawable(R.drawable.wall).toBitmap()
//            nativeTexture(holder.surface, bitmap)

//            Jni.nativeCreateProgram(glContext, "shader/v_simple_m.glsl", "shader/f_yuv2rgb.glsl")
//            nativeLoadYuv2(glContext)

            Jni.nativeCreateProgram(glContext, "shader/v_simple.glsl", "shader/f_yuv2rgb.glsl")
            nativeLoadYuv(glContext)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        handler.post {
            Jni.nativeDestroyGLContext(glContext)
        }
    }


    private external fun nativeApiTest(glContext: Long)
    private external fun nativeTexture(surface: Surface, bitmap: Bitmap)
    private external fun nativeLoadYuv(glContext: Long)
    private external fun nativeLoadYuv2(glContext: Long)


    companion object {
        const val TAG = "SurfaceViewTest"

        init {
            System.loadLibrary("gl_basics")
        }
    }
}
