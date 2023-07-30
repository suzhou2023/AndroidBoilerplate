package com.bbt2000.boilerplate.demos.gles._01_basics

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 *  author : suzhou
 *  date : 2023/7/16
 *  description : 对于GLSurfaceView的认识：在setRenderer之后，会创建renderer线程，
 *  并配置EGL相关的环境，之后就可以在renderer的回调方法中，利用opengl相关的api进行绘制了，
 *  renderer的回调都是在renderer线程当中运行的。
 *  GLSurfaceView其实就是封装了SurfaceView + EGL，我们自己完全可以在SurfaceView的基础上，
 *  利用EGL的api封装我们自己的GLSurfaceView。
 */
class GLSurfaceViewTest(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    init {
        setEGLContextClientVersion(3)
        setRenderer(GLRenderer())
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    inner class GLRenderer : Renderer {
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            Log.d(TAG, "onSurfaceCreated: ${Thread.currentThread().name}")
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        }
    }

    companion object {
        const val TAG = "GLSurfaceViewTest"
    }
}
