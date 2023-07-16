package com.bbt2000.boilerplate.demos.gles._02_native_egl_triangle

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView


/**
 *  author : suzhou
 *  date : 2023/7/16
 *  description :
 */
class SurfaceViewTest(context: Context, attrs: AttributeSet? = null) :
    SurfaceView(context, attrs), SurfaceHolder.Callback {

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawTriangleWithNativeEGL(holder.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    private external fun drawTriangleWithNativeEGL(surface: Any)


    companion object {
        const val TAG = "SurfaceViewTest"

        init {
            System.loadLibrary("native_egl_triangle")
        }
    }
}
