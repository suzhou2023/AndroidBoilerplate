package com.bbt2000.boilerplate.demos.gles._05_camera

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.Matrix
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import com.bbt2000.boilerplate.demos.gles.widget.AutoFitSurfaceView


/**
 *  author : suzhou
 *  date : 2023/7/16
 *  description :
 */
class SurfaceViewTest(context: Context, attrs: AttributeSet? = null) : AutoFitSurfaceView(context, attrs),
    SurfaceHolder.Callback {

    private var mHandlerThread: HandlerThread? = null;
    private var mHandler: Handler? = null;
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mPreviewRotation: Float? = null
    private var mWindowRatioWH: Float? = null
    private var mPreviewRatioWH: Float? = null

    init {
        holder.addCallback(this)
        mHandlerThread = HandlerThread("gl_render").apply { start() }
        mHandler = Handler(mHandlerThread!!.looper)
    }

    fun isAvailable() = mSurfaceTexture != null
    fun getTexture() = mSurfaceTexture
    fun setPreviewRatioWH(ratio: Float) {
        mPreviewRatioWH = ratio
        setMatrix()
    }

    fun setPreviewRotation(rotation: Float) {
        mPreviewRotation = rotation
        setMatrix()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "Surface created.")
        mHandler?.post {
            val textureId = nativeSurfaceCreated(holder.surface)
            if (textureId < 0) return@post
            mSurfaceTexture = SurfaceTexture(textureId)
            mSurfaceTexture?.setOnFrameAvailableListener {
                mSurfaceTexture?.updateTexImage()
                nativeDrawFrame()
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d(TAG, "Surface changed: width=$width,height=$height")
        mWindowRatioWH = width / height.toFloat()
    }

    private fun setMatrix() {
        if (mPreviewRotation == null || mPreviewRatioWH == null || mWindowRatioWH == null) return
        mHandler?.post {
            val matrix = FloatArray(16)
            Matrix.setIdentityM(matrix, 0)
            Log.d(TAG, "mPreviewRotation=$mPreviewRotation")
            Matrix.setRotateM(matrix, 0, mPreviewRotation!!, 0f, 0f, -1f)
            if (mWindowRatioWH!! > mPreviewRatioWH!!) {
                val scaleY = mWindowRatioWH!! / mPreviewRatioWH!!
                Matrix.scaleM(matrix, 0, 1f, scaleY, 1f)
            } else {
                val scaleX = mPreviewRatioWH!! / mWindowRatioWH!!
                Matrix.scaleM(matrix, 0, scaleX, 1f, 1f)
            }
            nativeSurfaceChanged(width, height, matrix)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(TAG, "Surface destroyed.")
        mHandler?.post { nativeSurfaceDestroyed() }
    }


    private external fun nativeSurfaceCreated(surface: Any): Int
    private external fun nativeSurfaceChanged(width: Int, height: Int, matrix: FloatArray)
    private external fun nativeSurfaceDestroyed()
    private external fun nativeDrawFrame()

    companion object {
        const val TAG = "SurfaceViewTest"

        init {
            System.loadLibrary("gl_render")
        }
    }
}
