package com.bbt2000.boilerplate.demos.gles._02_camera

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.Matrix
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.SurfaceHolder
import com.bbt2000.boilerplate.demos.gles._02_camera.encode.H264Encoder
import com.bbt2000.boilerplate.demos.gles._02_camera.jni.Jni.nativeConfigGL
import com.bbt2000.boilerplate.demos.gles._02_camera.jni.Jni.nativeCreateFbo
import com.bbt2000.boilerplate.demos.gles._02_camera.jni.Jni.nativeCreateGLContext
import com.bbt2000.boilerplate.demos.gles._02_camera.jni.Jni.nativeCreateOESTexture
import com.bbt2000.boilerplate.demos.gles._02_camera.jni.Jni.nativeDestroyGLContext
import com.bbt2000.boilerplate.demos.gles._02_camera.jni.Jni.nativeDrawFrame
import com.bbt2000.boilerplate.demos.gles._02_camera.jni.Jni.nativeEGLCreateSurface
import com.bbt2000.boilerplate.demos.gles._02_camera.jni.Jni.nativeSetMatrix
import com.bbt2000.boilerplate.demos.gles._02_camera.jni.Jni.nativeSurfaceChanged
import com.bbt2000.boilerplate.demos.gles.widget.AutoFitSurfaceView


/**
 *  author : suzhou
 *  date : 2023/7/16
 *  description :
 */
class SurfaceViewGL(context: Context, attrs: AttributeSet? = null) :
    AutoFitSurfaceView(context, attrs), SurfaceHolder.Callback {

    private var mGLContext: Long = 0;
    private val mHandlerThread: HandlerThread by lazy { HandlerThread("gl_render").apply { start() } }
    private val mHandler: Handler = Handler(mHandlerThread.looper)
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mPreviewRotation: Int? = null
    private var mPreviewSize: Size? = null
    private var mWindowSize: Size? = null
    private var mIsFrontCamera: Boolean = false
    private var mCallback: Callback? = null

    init {
        holder.addCallback(this)
    }

    interface Callback {
        fun onSurfaceChanged(size: Size)
        fun onTextureAvailable(texture: SurfaceTexture)
    }

    fun setCallback(callback: Callback) {
        mCallback = callback
    }

    fun setPreviewRotation(rotation: Int) {
        mPreviewRotation = rotation
        setMatrix()
    }

    fun setPreviewSize(previewSize: Size) {
        mPreviewSize = previewSize
        setMatrix()
        if (mSurfaceTexture != null) {
            mSurfaceTexture!!.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
            mCallback?.onTextureAvailable(mSurfaceTexture!!)
        }
    }

    fun setIsFrontCamera(isFront: Boolean) {
        mIsFrontCamera = isFront
    }

    fun createEncodeSurface(surface: Surface) {
        if (mGLContext <= 0) return
        mHandler.post {
            nativeEGLCreateSurface(mGLContext, surface, 1)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "Surface created.")
        mHandler.post {
            mGLContext = nativeCreateGLContext()
            if (mGLContext <= 0) return@post
            val success = nativeEGLCreateSurface(mGLContext, holder.surface, 0)
            if (!success) return@post
            nativeConfigGL(mGLContext)
            val oesTexture = nativeCreateOESTexture(mGLContext)
            if (oesTexture < 0) return@post
            mSurfaceTexture = SurfaceTexture(oesTexture)
            mSurfaceTexture?.setOnFrameAvailableListener {
                mSurfaceTexture ?: return@setOnFrameAvailableListener
                mSurfaceTexture?.updateTexImage()
                nativeDrawFrame(mGLContext)
            }
            if (mPreviewSize != null) {
                mSurfaceTexture!!.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
                mCallback?.onTextureAvailable(mSurfaceTexture!!)
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d(TAG, "Surface changed: width = $width, height = $height")
        mWindowSize = Size(width, height)
        mCallback?.onSurfaceChanged(mWindowSize!!)
        setMatrix()
        mHandler.post {
            nativeSurfaceChanged(mGLContext, format, width, height)
            nativeCreateFbo(mGLContext, width, height)
        }
    }


    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(TAG, "Surface destroyed.")
        mHandler.post {
            nativeDestroyGLContext(mGLContext)
            mSurfaceTexture?.release()
            mSurfaceTexture = null
        }
    }

    private fun setMatrix() {
        if (mPreviewRotation != null && mPreviewSize != null && mWindowSize != null) {
            mHandler.post {
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
                val windowRatio = mWindowSize!!.width / mWindowSize!!.height.toFloat()
                Log.d(TAG, "windowRatio = $windowRatio")
                var previewRatio = mPreviewSize!!.width / mPreviewSize!!.height.toFloat()
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
                nativeSetMatrix(mGLContext, matrix)
            }
        }
    }

    companion object {
        const val TAG = "SurfaceViewGL"
    }
}
