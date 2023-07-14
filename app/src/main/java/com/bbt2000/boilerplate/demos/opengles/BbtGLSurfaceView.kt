package com.bbt2000.boilerplate.demos.opengles

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *  author : sz
 *  date : 2023/7/13 18:22
 *  description :
 */

class BbtGLSurfaceView : GLSurfaceView, GLSurfaceView.Renderer {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        setEGLContextClientVersion(3)
        _nativeRenderer = NativeRenderer()
        setRenderer(this)
    }

    private var _nativeRenderer: NativeRenderer

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        _nativeRenderer.native_OnSurfaceCreated()
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        _nativeRenderer.native_OnSurfaceChanged(p1, p2)
    }

    override fun onDrawFrame(p0: GL10?) {
        _nativeRenderer.native_OnDrawFrame()
    }

    companion object {
        private const val TAG = "BbtGLSurfaceView"
    }
}

