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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bbt2000.boilerplate.R
import com.bbt2000.boilerplate.demos.gles._02_camera.encode.EncodeManager
import com.bbt2000.boilerplate.demos.gles._02_camera.encode.EncodeState
import com.bbt2000.boilerplate.demos.gles._02_camera.encode.VideoEncoder
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private val mCameraThread by lazy { HandlerThread("CameraThread").apply { start() } }
    private val mCameraHandler by lazy { Handler(mCameraThread.looper) }

    private lateinit var mSurfaceViewGL: SurfaceViewGL
    private var mPreviewWindowSize: Size? = null
    private var mPreviewSize: Size? = null
    private var mSurfaceOES: Surface? = null

    private var mEncodeManager: EncodeManager? = null
    private val mEncodeState by lazy { mutableStateOf(EncodeState.STOPPED) }
    private val mTimeCountMs by lazy { mutableStateOf(0L) }


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
                                mSurfaceOES = Surface(texture)
                                createCaptureSession()
                            }
                        })
                        rootView
                    },
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.White),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Box(modifier = Modifier.weight(1.0f)) {

                    }
                    Button(
                        onClick = { requirePermissions() }
                    ) {
                        val text = if (mEncodeState.value == EncodeState.STOPPED) "Record" else "Stop"
                        Text(text = text)
                    }
                    Box(modifier = Modifier.weight(1.0f)) {
                        Button(
                            modifier = Modifier.align(Alignment.Center),
                            onClick = { pauseOrResumeRecord() }
                        ) {
                            val text = if (mEncodeState.value == EncodeState.PAUSED) "Resume" else "Pause"
                            Text(text = text)
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.White)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.TopCenter),
                        text = "${mTimeCountMs.value / 1000}s"
                    )
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
                if (mPreviewSize != null) {
                    mSurfaceViewGL.setPreviewSize(mPreviewSize!!)
                }
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

    private fun requirePermissions() {
        val isAudioGranted = PermissionX.isGranted(this, Manifest.permission.RECORD_AUDIO)
        val isStorageGranted = PermissionX.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (isAudioGranted && isStorageGranted) {
            startOrStopRecord()
        } else {
            PermissionX
                .init(this)
                .permissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        startOrStopRecord()
                    }
                }
        }
    }

    private fun startOrStopRecord() {
        if (mEncodeManager == null) {
            mPreviewWindowSize ?: return
            val width = mPreviewWindowSize!!.width - mPreviewWindowSize!!.width % 16
            val height = mPreviewWindowSize!!.height - mPreviewWindowSize!!.height % 16
            mEncodeManager = EncodeManager(videoEnabled = false, audioEnabled = true, width, height)
            mEncodeManager?.setStateCallback {
                when (it) {
                    EncodeState.STOPPED -> {
                        mEncodeState.value = EncodeState.STOPPED
                        stopRecordTimeCount(true)
                    }

                    EncodeState.STARTED -> {
                        mEncodeState.value = EncodeState.STARTED
                        startRecordTimeCount()
                    }

                    EncodeState.PAUSED -> {
                        mEncodeState.value = EncodeState.PAUSED
                        stopRecordTimeCount()
                    }
                }
            }
            mEncodeManager?.start { mSurfaceViewGL.createEncodeSurface(it) }
        } else {
            if (mEncodeManager?.getEncodeState() == EncodeState.STOPPED) {
                mEncodeManager?.start { mSurfaceViewGL.createEncodeSurface(it) }
            } else {
                mEncodeManager?.stop()
            }
        }
    }

    private fun pauseOrResumeRecord() {
        mEncodeManager ?: return
        if (mEncodeManager?.getEncodeState() == EncodeState.STARTED) {
            mEncodeManager?.pause()
            return
        }
        if (mEncodeManager?.getEncodeState() == EncodeState.PAUSED) {
            mEncodeManager?.resume()
            return
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

    private fun createCaptureSession() {
        if (mCameraDevice == null || mSurfaceOES == null) return
        try {
            mCameraDevice?.createCaptureSession(
                listOf(mSurfaceOES), sessionStateCallback, mCameraHandler
            )
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to create session.", t)
        }
    }


    private val sessionStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
            Log.d(TAG, "Capture session configured.")
            mCameraCaptureSession = cameraCaptureSession
            startPreview()
        }

        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
            Log.e(TAG, "Failed to configure session.")
        }
    }

    private fun startPreview() {
        try {
            mCameraDevice ?: return
            val previewRequestBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(mSurfaceOES!!)
            previewRequestBuilder.set(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO
            )
            mCameraCaptureSession?.setRepeatingRequest(
                previewRequestBuilder.build(), null, mCameraHandler
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open camera preview.", e)
            e.printStackTrace()
        }
    }

    private fun release() {
        try {
            mSurfaceOES?.release()
            mSurfaceOES = null
            mEncodeManager?.release()
            mEncodeManager = null
            mCameraDevice?.close()
            mCameraDevice = null
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to release resources.", t)
        }
    }

    private var mTimeJob: Job? = null
    private fun startRecordTimeCount() {
        mTimeJob = GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Default) {
                while (isActive) {
                    delay(50)
                    mTimeCountMs.value += 50
                }
            }
        }
    }

    private fun stopRecordTimeCount(reset: Boolean = false) {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Default) {
                mTimeJob?.cancelAndJoin()
                mTimeJob = null
                if (reset) mTimeCountMs.value = 0L
            }
        }
    }

    companion object {
        private const val TAG = "DemoActivity"
        private const val CameraFacing = CameraCharacteristics.LENS_FACING_FRONT
    }
}

