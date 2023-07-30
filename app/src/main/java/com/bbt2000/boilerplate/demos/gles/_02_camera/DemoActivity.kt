package com.bbt2000.boilerplate.demos.gles._02_camera

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
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bbt2000.boilerplate.R
import com.bbt2000.boilerplate.demos.gles._02_camera.encode.EncodeState
import com.bbt2000.boilerplate.demos.gles._02_camera.encode.H264Encoder
import com.permissionx.guolindev.PermissionX


/**
 *  author : sz
 *  date : 2023/7/13 14:54
 *  description : Camera2 api
 */
class DemoActivity : AppCompatActivity() {
    private var mCameraId: String? = null
    private var mCameraDevice: CameraDevice? = null
    private val mCameraManager: CameraManager by lazy {
        applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    private val mCameraThread = HandlerThread("CameraThread").apply { start() }
    private val mCameraHandler = Handler(mCameraThread.looper)

    private var mH264Encoder: H264Encoder? = null
    private lateinit var mSurfaceViewGL: SurfaceViewGL
    private var mPreviewWindowSize: Size? = null
    private var mPreviewSize: Size? = null
    private var mPreviewSurface: Surface? = null
    private var mEncodeSurface: Surface? = null


    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            Column(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    modifier = Modifier
                        .weight(weight = 1f),
                    factory = {
                        val rootView = LayoutInflater.from(it).inflate(R.layout.layout_surfaceviewgl, null)
                        mSurfaceViewGL = rootView.findViewById(R.id.surfaceView)
                        mSurfaceViewGL.setCallback(object : SurfaceViewGL.Callback {
                            override fun onSurfaceChanged(size: Size) {
                                mPreviewWindowSize = size
                                configurePreview()
                                requireCameraPermission()
                            }

                            override fun onTextureAvailable(texture: SurfaceTexture) {
                                mPreviewSurface = Surface(texture)
                                createCaptureSession()
                            }
                        })
                        rootView
                    },
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.White)
                ) {
                    Button(
                        modifier = Modifier
                            .align(Alignment.Center),
                        onClick = {
                            requireStoragePermission()
                        }
                    ) {
                        Text(text = "Record")
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        release()
    }

    private fun configurePreview() {
        for (id in mCameraManager.cameraIdList) {
            Log.d(TAG, "Camera id = $id")
            val displayRotation = windowManager.defaultDisplay.rotation
            val characteristics = mCameraManager.getCameraCharacteristics(id)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            // 注意有的手机有多颗后置镜头
            if (facing == CameraFacing) {
                mCameraId = id
                // 预览方向
                val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
                if (sensorOrientation != null) {
                    Log.i(TAG, "Sensor orientation = $sensorOrientation")
                    if (CameraFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        val previewRotation = (sensorOrientation - displayRotation) % 360
                        mSurfaceViewGL.setPreviewRotation(previewRotation)
                        mSurfaceViewGL.setIsFrontCamera(false)
                    } else {
                        val previewRotation = (sensorOrientation + displayRotation) % 360
                        mSurfaceViewGL.setPreviewRotation(previewRotation)
                        mSurfaceViewGL.setIsFrontCamera(true)
                    }
                }
                // 预览尺寸配置
                if (mPreviewWindowSize == null) return
                mPreviewSize = CameraUtil.choosePreviewSize(mPreviewWindowSize!!, characteristics)
                if (mPreviewSize == null) return
                mSurfaceViewGL.setPreviewSize(mPreviewSize!!)
                // 录像配置
                mH264Encoder = H264Encoder(mPreviewSize!!.width, mPreviewSize!!.height)
                mH264Encoder?.setCallback(object : H264Encoder.Callback {
                    override fun onSurfaceAvailable(surface: Surface) {
                        mEncodeSurface = surface
                        mSurfaceViewGL.setCodecInputSurface(mEncodeSurface!!)
//                        createCaptureSession()
                    }

                    override fun onConfigured() {}
                })
                mH264Encoder?.configure()
                break
            }
        }
    }

    private fun requireCameraPermission() {
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

    private fun requireStoragePermission() {
        val isGranted = PermissionX.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (isGranted) {
            startOrStopRecord()
        } else {
            PermissionX
                .init(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        startOrStopRecord()
                    }
                }
        }
    }

    private fun startOrStopRecord() {
        if (mH264Encoder!!.getEncodeState() != EncodeState.STARTED) {
            mH264Encoder?.start()
            createCaptureSession2()
        } else {
            mH264Encoder?.stop()
        }
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        if (PermissionX.isGranted(this, Manifest.permission.CAMERA)) {
            mCameraManager.openCamera(mCameraId!!, cameraStateCallback, mCameraHandler)
        }
    }

    private val cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            Log.i(TAG, "Camera opened.")
            mCameraDevice = camera
            createCaptureSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.i(TAG, "Camera disconnected.")
            mCameraDevice = camera
            release()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.e(TAG, "On error: $error")
        }
    }

    /**
     * 创建capture session需要三方都准备好：1.相机 2:预览surface 3.录像surface
     * 因为不知道先后顺序，所以有哪一方准备好了，都来尝试调一下这个方法，最后一个才能成功
     */
    private fun createCaptureSession() {
        if (mCameraDevice == null || mPreviewSurface == null) return
        try {
            mCameraDevice?.createCaptureSession(
                listOf(mPreviewSurface), sessionStateCallback, mCameraHandler
            )
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to create session.", t)
        }
    }

    private fun createCaptureSession2() {
        if (mCameraDevice == null || mEncodeSurface == null) return
        try {
            mCameraDevice?.createCaptureSession(
                listOf(mEncodeSurface), sessionStateCallback2, mCameraHandler
            )
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to create session.", t)
        }
    }

    private val sessionStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
            Log.d(TAG, "Capture session configured.")
            try {
                val previewRequestBuilder = mCameraDevice!!.createCaptureRequest(
                    CameraDevice.TEMPLATE_PREVIEW
                )
                previewRequestBuilder.addTarget(mPreviewSurface!!)
                previewRequestBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO
                )
                cameraCaptureSession.setRepeatingRequest(
                    previewRequestBuilder.build(), null, mCameraHandler
                )
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to open camera preview.", t)
            }
        }

        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
            Log.e(TAG, "Failed to configure session.")
        }
    }

    private val sessionStateCallback2 = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
            Log.d(TAG, "Capture session configured.")
            try {
                val previewRequestBuilder = mCameraDevice!!.createCaptureRequest(
                    CameraDevice.TEMPLATE_PREVIEW
                )
//                previewRequestBuilder.addTarget(mPreviewSurface!!)
                previewRequestBuilder.addTarget(mEncodeSurface!!)
                previewRequestBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO
                )
                cameraCaptureSession.setRepeatingRequest(
                    previewRequestBuilder.build(), null, mCameraHandler
                )
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to open camera preview.", t)
            }
        }

        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
            Log.e(TAG, "Failed to configure session.")
        }
    }

    private fun release() {
        try {
            mPreviewSurface?.release()
            mH264Encoder?.release()
            mCameraDevice?.close()
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to release resources.", t)
        }
    }

    companion object {
        private const val TAG = "DemoActivity"
        private const val CameraFacing = CameraCharacteristics.LENS_FACING_BACK
    }
}

