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

class BbtGLSurfaceView : GLSurfaceView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    private val mGLRender: GLRender
    private val mNativeRender: NativeRender

    init {
        setEGLContextClientVersion(3)
        mNativeRender = NativeRender()
        mGLRender = GLRender(mNativeRender)
        setRenderer(mGLRender)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    class GLRender internal constructor(nativeRender: NativeRender) : Renderer {
        private val mNativeRender: NativeRender

        init {
            mNativeRender = nativeRender
        }

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            mNativeRender.native_OnSurfaceCreated()
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            mNativeRender.native_OnSurfaceChanged(width, height)
        }

        override fun onDrawFrame(gl: GL10) {
            mNativeRender.native_OnDrawFrame()
        }
    }
}

