package com.bbt2000.boilerplate.demos.opengles

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
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
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 *  author : sz
 *  date : 2023/7/13 14:54
 *  description :
 */

class DemoActivity : AppCompatActivity() {
    var mCameraId: String? = null
    var mCameraDevice: CameraDevice? = null
    lateinit var mTextureView: TextureView

    private val mMainLooperHandler = Handler(Looper.getMainLooper())
    private val mCameraThread = HandlerThread("MyCameraThread").apply { start() }
    private val mCameraHandler = Handler(mCameraThread.looper)
    private val mCameraManager: CameraManager by lazy {
        applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private var mSurface: Surface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_camera)
        mTextureView = findViewById(R.id.texture_view)

        for (id in mCameraManager.cameraIdList) {
            val characteristics = mCameraManager.getCameraCharacteristics(id)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                mCameraId = id
            }
        }

        if (PermissionX.isGranted(this, Manifest.permission.CAMERA)) {
            openCamera()
        } else {
            PermissionX
                .init(this)
                .permissions(Manifest.permission.CAMERA)
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        openCamera()
                    }
                }
        }
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
                    if (mTextureView.isAvailable) {
                        createCaptureSession()
                    }
                    mTextureView.surfaceTextureListener = surfaceTextureListener
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
            Log.e(TAG, "on Error: $error")
        }
    }

    private fun createCaptureSession() {
        if (mCameraDevice == null || !mTextureView.isAvailable) return

        val targetTexture = mCameraManager.getCameraCharacteristics(mCameraId!!).let {
            CameraUtils.buildTargetTexture(mTextureView, it)
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

    private fun release() {
        try {
            mSurface?.release()
            mCameraDevice?.close()
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to release resources.", t)
        }
    }

    private val sessionStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
            try {
                val captureRequest = mCameraDevice?.createCaptureRequest(
                    CameraDevice.TEMPLATE_PREVIEW
                )
                captureRequest?.addTarget(mSurface!!)
                cameraCaptureSession.setRepeatingRequest(
                    captureRequest?.build()!!, null, mCameraHandler
                )
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to open camera preview.", t)
            }
        }

        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
            Log.e(TAG, "Failed to configure camera.")
        }
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            createCaptureSession()
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            /*** Codelab -> start a session here ***/
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = false
    }

    companion object {
        const val TAG = "Camera2"
    }

}

