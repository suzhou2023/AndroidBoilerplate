package com.bbt2000.boilerplate.demos.gles._05_camera

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
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

    private var mSurfaceTexture: SurfaceTexture? = null
    private val rotateMatrix = FloatArray(16)

    fun isAvailable() = mSurfaceTexture != null
    fun getTexture() = mSurfaceTexture

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "Surface created.")

        nativeSurfaceCreated(holder.surface)
        val textureId = nativeCreateTexture(holder.surface)
        mSurfaceTexture = SurfaceTexture(textureId)
        mSurfaceTexture?.setOnFrameAvailableListener {
            mSurfaceTexture?.updateTexImage()
            nativeDrawFrame()
//                mSurfaceTexture?.getTransformMatrix(rotateMatrix)
//                Log.d(TAG, "rotateMatrix = ${rotateMatrix.contentToString()}")
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }


    private external fun nativeSurfaceCreated(surface: Any)
    private external fun nativeCreateTexture(surface: Any): Int
    private external fun nativeDrawFrame()


    companion object {
        const val TAG = "SurfaceViewTest"

        init {
            System.loadLibrary("gl_render")
        }
    }
}
