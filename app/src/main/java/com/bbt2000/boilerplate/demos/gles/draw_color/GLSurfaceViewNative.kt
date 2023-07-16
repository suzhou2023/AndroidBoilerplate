package com.bbt2000.boilerplate.demos.gles.draw_color

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 *  author : suzhou
 *  date : 2023/7/16
 *  description :
 */
class GLSurfaceViewNative(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs), Runnable, SurfaceHolder.Callback, GLSurfaceView.Renderer {

    init {
        setRenderer(this)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(TAG, "onAttachedToWindow: ${System.currentTimeMillis()}")
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "surfaceCreated: ${System.currentTimeMillis()}")
        Log.d(TAG, "currentThread: ${Thread.currentThread().name}")
        Thread(this).start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {}

    override fun onDrawFrame(gl: GL10?) {}

    override fun run() {
        drawColorNative(holder.surface, 250)
    }

    private external fun drawColorNative(surface: Any, color: Int)

    companion object {
        const val TAG = "GLSurfaceViewNative"

        init {
            System.loadLibrary("camera")
        }
    }
}
