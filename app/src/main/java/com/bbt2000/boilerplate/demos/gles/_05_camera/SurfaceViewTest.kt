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
class SurfaceViewTest(context: Context, attrs: AttributeSet? = null) :
    AutoFitSurfaceView(context, attrs), SurfaceHolder.Callback {

    private var mHandlerThread: HandlerThread? = null;
    private var mHandler: Handler? = null;
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mPreviewRotation: Int? = null
    private var mPreviewWidth: Int? = null
    private var mPreviewHeight: Int? = null
    private var mWindowWidth: Int? = null
    private var mWindowHeight: Int? = null
    private var mIsFrontCamera: Boolean = false

    init {
        holder.addCallback(this)
        mHandlerThread = HandlerThread("gl_render").apply { start() }
        mHandler = Handler(mHandlerThread!!.looper)
    }

    fun isAvailable(): Boolean {
        synchronized(this) {
            return mSurfaceTexture != null
        }
    }

    fun getTexture() = mSurfaceTexture
    fun setPreviewRotation(rotation: Int) {
        mPreviewRotation = rotation
        setMatrix()
    }

    fun setPreviewSize(width: Int, height: Int) {
        mPreviewWidth = width
        mPreviewHeight = height
        setMatrix()
    }

    fun setIsFrontCamera(isFront: Boolean) {
        mIsFrontCamera = isFront
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "Surface created.")
        mHandler?.post {
            synchronized(this) {
                val textureId = nativeSurfaceCreated(holder.surface)
                if (textureId < 0) return@post
                mSurfaceTexture = SurfaceTexture(textureId)
                mSurfaceTexture?.setOnFrameAvailableListener {
                    mSurfaceTexture?.updateTexImage()
                    nativeDrawFrame()
                }
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d(TAG, "Surface changed: width = $width, height = $height")
        mWindowWidth = width;
        mWindowHeight = height;
        setMatrix()
    }


    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(TAG, "Surface destroyed.")
        mSurfaceTexture?.release()
        mSurfaceTexture = null
        mHandler?.post { nativeSurfaceDestroyed() }
    }

    private fun setMatrix() {
        if (mPreviewRotation != null && mPreviewWidth != null && mPreviewHeight != null
            && mWindowWidth != null && mWindowHeight != null
        ) {
            mHandler?.post {
                val matrix = FloatArray(16)
                Matrix.setIdentityM(matrix, 0)
                Log.d(TAG, "mPreviewRotation = $mPreviewRotation")
                // 旋转角度
                Matrix.setRotateM(matrix, 0, mPreviewRotation!!.toFloat(), 0f, 0f, -1f)
                // OES纹理坐标到底是什么方向？
                // 前摄要不要左右镜面翻转？
                if (mIsFrontCamera)
                    Matrix.scaleM(matrix, 0, 1f, -1f, 1f)
                // 根据窗口尺寸和预览尺寸，设置缩放
                val windowRatio = mWindowWidth!! / mWindowHeight!!.toFloat()
                Log.d(TAG, "windowRatio = $windowRatio")
                var previewRatio = mPreviewWidth!! / mPreviewHeight!!.toFloat()
                Log.d(TAG, "previewRatio = $previewRatio")
                if (mPreviewRotation!! % 180 == 90) previewRatio = 1 / previewRatio
                Log.d(TAG, "1/previewRatio = $previewRatio")
                if (windowRatio > previewRatio) {
                    // todo: 为什么是x坐标放大
                    val scaleX = windowRatio / previewRatio
                    Log.d(TAG, "scaleX = $scaleX")
                    Matrix.scaleM(matrix, 0, scaleX, 1f, 1f)
                } else {
                    val scaleY = previewRatio / windowRatio
                    Log.d(TAG, "scaleY = $scaleY")
                    Matrix.scaleM(matrix, 0, 1f, scaleY, 1f)
                }
                nativeSetMatrix(matrix)
            }
        }
    }

    private external fun nativeSurfaceCreated(surface: Any): Int
    private external fun nativeSurfaceDestroyed()
    private external fun nativeSetMatrix(matrix: FloatArray)
    private external fun nativeDrawFrame()

    companion object {
        const val TAG = "SurfaceViewTest"

        init {
            System.loadLibrary("gl_render")
        }
    }
}
