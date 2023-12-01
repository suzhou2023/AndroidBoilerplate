package com.bbt2000.boilerplate.demos.gles._02_camera

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.SurfaceHolder
import com.bbt2000.gles.jni.JniGL
import com.bbt2000.gles.widget.AutoFitSurfaceView


/**
 *  author : suzhou
 *  date : 2023/7/16
 *  description :
 */
class SurfaceViewGL(context: Context, attrs: AttributeSet? = null) :
    AutoFitSurfaceView(context, attrs), SurfaceHolder.Callback {

    private var mGLContext: Long = 0
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
    }

    fun setPreviewSize(previewSize: Size) {
        mPreviewSize = previewSize
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
            JniGL.createEGLSurface(mGLContext, surface, 1)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "Surface created.")
        mHandler.post {
            mGLContext = JniGL.createGLContext(assetManager = context.assets)
            if (mGLContext <= 0) return@post
            val success = JniGL.createEGLSurface(mGLContext, holder.surface, 0)
            if (!success) return@post
            JniGL.createProgram(mGLContext, "shader/v_simple_m.glsl", "shader/f_oes.glsl")
            JniGL.loadVertices(mGLContext)
            val oesTexture = JniGL.createOESTexture(mGLContext)
            if (oesTexture < 0) return@post
            mSurfaceTexture = SurfaceTexture(oesTexture)
            mSurfaceTexture?.setOnFrameAvailableListener {
                mSurfaceTexture ?: return@setOnFrameAvailableListener
                mSurfaceTexture?.updateTexImage()
                // todo
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
        mHandler.post {
            JniGL.createFbo(mGLContext, width, height, 0)
        }
    }


    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(TAG, "Surface destroyed.")
        mHandler.post {
            JniGL.destroyGLContext(mGLContext)
            mSurfaceTexture?.release()
            mSurfaceTexture = null
        }
    }


    companion object {
        private val TAG = SurfaceViewGL::class.simpleName

        init {
            System.loadLibrary("gl_render")
        }
    }
}
