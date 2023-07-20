package com.bbt2000.boilerplate.demos.gles._05_camera

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.HandlerThread
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

    private var mHandlerThread: HandlerThread? = null;
    private var mHandler: Handler? = null;
    private var mSurfaceTexture: SurfaceTexture? = null
    private val rotateMatrix = FloatArray(16)

    init {
        holder.addCallback(this)
        mHandlerThread = HandlerThread("gl_render").apply { start() }
        mHandler = Handler(mHandlerThread!!.looper)
    }


    fun isAvailable() = mSurfaceTexture != null
    fun getTexture() = mSurfaceTexture

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "Surface created.")
        mHandler?.post {
            nativeSurfaceCreated(holder.surface)
            val textureId = nativeCreateTexture()
            mSurfaceTexture = SurfaceTexture(textureId)
            mSurfaceTexture?.setOnFrameAvailableListener {
                mSurfaceTexture?.updateTexImage()
                nativeDrawFrame()
//                mSurfaceTexture?.getTransformMatrix(rotateMatrix)
//                Log.d(TAG, "rotateMatrix = ${rotateMatrix.contentToString()}")
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d(TAG, "Surface changed: width=$width,height=$height")
        mHandler?.post { nativeSurfaceChanged(width, height) }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(TAG, "Surface destroyed.")
        mHandler?.post { nativeSurfaceDestroyed() }
        mHandlerThread?.quitSafely()
    }


    private external fun nativeSurfaceCreated(surface: Any)
    private external fun nativeSurfaceChanged(width: Int, height: Int)
    private external fun nativeSurfaceDestroyed()
    private external fun nativeCreateTexture(): Int
    private external fun nativeDrawFrame()

    companion object {
        const val TAG = "SurfaceViewTest"

        init {
            System.loadLibrary("gl_render")
        }
    }
}
