package com.bbt2000.boilerplate.demos.gles.egl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
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

    override fun surfaceCreated(holder: SurfaceHolder) {
        Thread(this).start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {}

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {}

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {}

    override fun onDrawFrame(gl: GL10?) {}

    override fun run() {
        drawTriangleNative(holder.surface)
    }

    external fun drawTriangleNative(surface: Any)

    companion object {
        init {
            System.loadLibrary("egl_triangle")
        }
    }
}
