package com.bbt2000.boilerplate.demos.gles.camera_gl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bbt2000.boilerplate.R
import com.bbt2000.boilerplate.demos.gles.util.CameraUtils
import com.bbt2000.boilerplate.demos.gles.widget.BbtGLSurfaceView
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 *  author : sz
 *  date : 2023/7/13 14:54
 *  description : Camera2 api
 */
class CameraActivity : AppCompatActivity() {
    private var mCameraId: String? = null
    private var mCameraDevice: CameraDevice? = null
    private val mCameraManager: CameraManager by lazy {
        applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private lateinit var mGLSurfaceView: BbtGLSurfaceView
    private var mSurface: Surface? = null

    private val mMainLooperHandler = Handler(Looper.getMainLooper())
    private val mCameraThread = HandlerThread("CameraThread").apply { start() }
    private val mCameraHandler = Handler(mCameraThread.looper)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_camera)
        mGLSurfaceView = findViewById(R.id.gl_surface_view)
//        mGLSurfaceView.surfaceTextureListener = surfaceTextureListener

        for (id in mCameraManager.cameraIdList) {
            val characteristics = mCameraManager.getCameraCharacteristics(id)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                mCameraId = id
                break
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (PermissionX.isGranted(this, Manifest.permission.CAMERA)) {
            openCamera()
        } else {
            PermissionX
                .init(this)
                .permissions(Manifest.permission.CAMERA)
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        openCamera()
                    }
                }
        }
    }

    override fun onPause() {
        super.onPause()
        release()
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(
            surface: SurfaceTexture, width: Int, height: Int
        ) {
            createCaptureSession()
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture, width: Int, height: Int
        ) {
            /*** Codelab -> start a session here ***/
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = false
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() = lifecycleScope.launch(Dispatchers.Main) {
        mCameraManager.openCamera(mCameraId!!, cameraStateCallback, mCameraHandler)
    }

    private val cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            try {
                mMainLooperHandler.post {
                    if (mGLSurfaceView.isAvailable()) {
                        createCaptureSession()
                    }
//                    mGLSurfaceView.surfaceTextureListener = surfaceTextureListener
                }
            } catch (t: Throwable) {
                release()
                Log.e(TAG, "Failed to initialize camera.", t)
            }
        }

        override fun onDisconnected(camera: CameraDevice) {
            mCameraDevice = camera
            release()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            mCameraDevice = camera
            Log.e(TAG, "On error: $error")
        }
    }

    private fun createCaptureSession() {
        if (mCameraDevice == null || !mGLSurfaceView.isAvailable()) return

        val targetTexture = mCameraManager.getCameraCharacteristics(mCameraId!!).let {
            CameraUtils.buildTargetTexture(mGLSurfaceView, it)
        }

        this.mSurface = Surface(targetTexture)
        try {
            mCameraDevice?.createCaptureSession(
                listOf(mSurface), sessionStateCallback, mCameraHandler
            )
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to create session.", t)
        }
    }

    private val sessionStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
            try {
                val previewRequestBuilder = mCameraDevice?.createCaptureRequest(
                    CameraDevice.TEMPLATE_PREVIEW
                )
                previewRequestBuilder?.addTarget(mSurface!!)
                previewRequestBuilder?.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                )
                cameraCaptureSession.setRepeatingRequest(
                    previewRequestBuilder?.build()!!, captureCallback, mCameraHandler
                )
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to open camera preview.", t)
            }
        }

        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
            Log.e(TAG, "Failed to configure camera.")
        }
    }

    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {

    }

    private fun release() {
        try {
            mSurface?.release()
            mCameraDevice?.close()
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to release resources.", t)
        }
    }

    companion object {
        const val TAG = "CameraActivity"
    }
}

