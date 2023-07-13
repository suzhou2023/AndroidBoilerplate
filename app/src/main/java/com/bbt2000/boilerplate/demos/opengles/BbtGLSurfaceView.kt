package com.bbt2000.boilerplate.demos.opengles

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
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

    fun getNativeRender(): NativeRender {
        return mNativeRender
    }

    class GLRender internal constructor(myNativeRender: NativeRender) : Renderer {
        private val mNativeRender: NativeRender

        init {
            mNativeRender = myNativeRender
        }

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            Log.d(TAG, "onSurfaceCreated() called with: gl = [$gl], config = [$config]")
            mNativeRender.native_OnSurfaceCreated()
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            Log.d(TAG, "onSurfaceChanged() called with: gl = [$gl], width = [$width], height = [$height]")
            mNativeRender.native_OnSurfaceChanged(width, height)
        }

        override fun onDrawFrame(gl: GL10) {
            Log.d(TAG, "onDrawFrame() called with: gl = [$gl]")
            mNativeRender.native_OnDrawFrame()
        }
    }

    companion object {
        private const val TAG = "BbtGLSurfaceView"

        const val IMAGE_FORMAT_RGBA = 0x01
        const val IMAGE_FORMAT_NV21 = 0x02
        const val IMAGE_FORMAT_NV12 = 0x03
        const val IMAGE_FORMAT_I420 = 0x04
    }
}

