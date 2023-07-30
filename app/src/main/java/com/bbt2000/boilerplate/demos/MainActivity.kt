package com.bbt2000.boilerplate.demos

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.Image
import android.media.ImageReader
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bbt2000.boilerplate.R
import java.io.IOException
import java.nio.ByteBuffer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var previewTextureView: TextureView? = null
    private var shutterClick: Button? = null
    private var previewSize: Size? = null
    private var mCameraId: String? = null
    private var cameraHandler: Handler? = null
    private var previewCaptureSession: CameraCaptureSession? = null
    private var videoMediaCodec: MediaCodec? = null
    private var audioRecord: AudioRecord? = null
    private var isRecordingVideo = false
    private var mWidth = 0
    private var mHeight = 0
    private var previewImageReader: ImageReader? = null
    private var previewCaptureHandler: Handler? = null
    private var videoRequest: CaptureRequest.Builder? = null
    private var nanoTime: Long = 0
    private var AudioiMinBufferSize = 0
    private var mIsAudioRecording = false
    private var AudioCodec: MediaCodec? = null
    private var mediaMuxer: MediaMuxer? = null

    @Volatile
    private var mAudioTrackIndex = -1

    @Volatile
    private var mVideoTrackIndex = -1

    @Volatile
    private var isMediaMuxerStarted = -1
    private var audioRecordThread: Thread? = null
    private var VideoCodecThread: Thread? = null
    private var AudioCodecThread: Thread? = null
    private var presentationTimeUs: Long = 0
    var captureListener: AudioCaptureListener? = null

    @Volatile
    private var isStop = 0
    var previewCaptureRequestBuilder: CaptureRequest.Builder? = null

    @Volatile
    private var videoMediaCodecIsStoped = 0

    @Volatile
    private var AudioCodecIsStoped = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        initView()
    }

    //1.初始化MediaCodec视频编码
    //设置mediaformat，到时候放到MediaRecodc里面
    private fun initMediaCodec(width: Int, height: Int) {
        Log.d(TAG, "width:$width")
        Log.d(TAG, "Height:$height")
        try {
            //先拿到格式容器
            /*
              MediaFormat.createVideoFormat中的宽高参数，不能为奇数
              过小或超过屏幕尺寸，也会出现这个错误
            */
            val videoFormat =
                MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1440, 1440)
            //设置色彩控件
            videoFormat.setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
            )
            //设置码率，码率就是数据传输单位时间传递的数据位数
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, 500000)
            //设置帧率
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20)
            //设置关键帧间隔
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
            videoFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4000000)
            //创建MediaCodc
            videoMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
            videoMediaCodec!!.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun initView() {
        previewTextureView = findViewById(R.id.previewtexture)
        shutterClick = findViewById(R.id.shutterclick)
        shutterClick!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        previewTextureView!!.surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                Log.d(TAG, "onSurfaceTextureAvailable")
                setupCamera(width, height)
            }

            override fun onSurfaceTextureSizeChanged(
                surfaceTexture: SurfaceTexture,
                i: Int,
                i1: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
        }
        val videoRecordThread = HandlerThread("VideoRecordThread")
        videoRecordThread.start()
        cameraHandler = object : Handler(videoRecordThread.looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
            }
        }
        val preivewImageReaderThread = HandlerThread("preivewImageReaderThread")
        preivewImageReaderThread.start()
        previewCaptureHandler = object : Handler(preivewImageReaderThread.looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
            }
        }
    }

    //设置预览的ImageReader
    private fun setPreviewImageReader() {
        previewImageReader = ImageReader.newInstance(
            previewSize!!.width,
            previewSize!!.height,
            ImageFormat.YUV_420_888,
            1
        )
        previewImageReader!!.setOnImageAvailableListener({ imageReader ->
            Log.d(TAG, "onImageAvailable")
            val image = imageReader.acquireNextImage()
            val width = image.width
            val height = image.height
            val I420size = width * height * 3 / 2
            Log.d(TAG, "I420size:$I420size")
            val nv21 = ByteArray(I420size)
            YUVToNV21_NV12(image, nv21, image.width, image.height, "NV21")
            encodeVideo(nv21)
            image.close()
        }, previewCaptureHandler)
    }

    private fun encodeVideo(nv21: ByteArray) {
        //输入
        val index = videoMediaCodec!!.dequeueInputBuffer(WAIT_TIME.toLong())
        //Log.d(TAG,"video encord video index:"+index);
        if (index >= 0) {
            val inputBuffer = videoMediaCodec!!.getInputBuffer(index)
            inputBuffer!!.clear()
            val remaining = inputBuffer.remaining()
            inputBuffer.put(nv21, 0, nv21.size)
            videoMediaCodec!!.queueInputBuffer(
                index,
                0,
                nv21.size,
                (System.nanoTime() - nanoTime) / 1000,
                0
            )
        }
    }

    private fun encodeVideoH264() {
        val videoBufferInfo = MediaCodec.BufferInfo()
        var videobufferindex = videoMediaCodec!!.dequeueOutputBuffer(videoBufferInfo, 0)
        Log.d(TAG, "videobufferindex:$videobufferindex")
        if (videobufferindex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            //添加轨道
            mVideoTrackIndex = mediaMuxer!!.addTrack(videoMediaCodec!!.outputFormat)
            Log.d(TAG, "mVideoTrackIndex:$mVideoTrackIndex")
            if (mAudioTrackIndex != -1) {
                Log.d(TAG, "encodeVideoH264:mediaMuxer is Start")
                mediaMuxer!!.start()
                isMediaMuxerStarted += 1
                setPCMListener()
            }
        } else {
            if (isMediaMuxerStarted >= 0) {
                while (videobufferindex >= 0) {
                    //获取输出数据成功
                    val videoOutputBuffer = videoMediaCodec!!.getOutputBuffer(videobufferindex)
                    Log.d(TAG, "Video mediaMuxer writeSampleData")
                    mediaMuxer!!.writeSampleData(
                        mVideoTrackIndex,
                        videoOutputBuffer!!,
                        videoBufferInfo
                    )
                    videoMediaCodec!!.releaseOutputBuffer(videobufferindex, false)
                    videobufferindex = videoMediaCodec!!.dequeueOutputBuffer(videoBufferInfo, 0)
                }
            }
        }
    }

    private fun encodePCMToAC() {
        val audioBufferInfo = MediaCodec.BufferInfo()
        //获得输出
        var audioBufferFlag = AudioCodec!!.dequeueOutputBuffer(audioBufferInfo, 0)
        Log.d(TAG, "CALL BACK DATA FLAG:$audioBufferFlag")
        if (audioBufferFlag == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            //這時候進行添加軌道
            mAudioTrackIndex = mediaMuxer!!.addTrack(AudioCodec!!.outputFormat)
            Log.d(TAG, "mAudioTrackIndex:$mAudioTrackIndex")
            if (mVideoTrackIndex != -1) {
                Log.d(TAG, "encodecPCMToACC:mediaMuxer is Start")
                mediaMuxer!!.start()
                isMediaMuxerStarted += 1
                //开始了再创建录音回调
                setPCMListener()
            }
        } else {
            Log.d(TAG, "isMediaMuxerStarted:$isMediaMuxerStarted")
            if (isMediaMuxerStarted >= 0) {
                while (audioBufferFlag >= 0) {
                    val outputBuffer = AudioCodec!!.getOutputBuffer(audioBufferFlag)
                    mediaMuxer!!.writeSampleData(mAudioTrackIndex, outputBuffer!!, audioBufferInfo)
                    AudioCodec!!.releaseOutputBuffer(audioBufferFlag, false)
                    audioBufferFlag = AudioCodec!!.dequeueOutputBuffer(audioBufferInfo, 0)
                }
            }
        }
    }

    private fun setPCMListener() {
        captureListener = object : AudioCaptureListener {
            override fun onCaptureListener(audioSource: ByteArray, audioReadSize: Int) {
                callbackData(audioSource, audioReadSize)
            }
        }
    }

    private fun setupCamera(width: Int, height: Int) {
        val cameraManage = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            val cameraIdList = cameraManage.cameraIdList
            for (cameraId in cameraIdList) {
                val cameraCharacteristics = cameraManage.getCameraCharacteristics(cameraId)
                //demo就就简单写写后摄录像
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) != CameraCharacteristics.LENS_FACING_BACK) {
                    //表示匹配到前摄，直接跳过这次循环
                    continue
                }
                val map =
                    cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                val outputSizes = map!!.getOutputSizes(
                    SurfaceTexture::class.java
                )
                val size = Size(1440, 1440)
                previewSize = size
                mWidth = previewSize!!.width
                mHeight = previewSize!!.height
                mCameraId = cameraId
            }
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
        openCamera()
    }

    @SuppressLint("MissingPermission")
    private fun initAudioRecord() {
        AudioiMinBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioRecord = AudioRecord.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_IN_MONO).build()
                )
                .setBufferSizeInBytes(AudioiMinBufferSize)
                .setAudioSource(MediaRecorder.AudioSource.MIC)
                .build()
        }
    }

    private fun openCamera() {
        Log.d(TAG, "openCamera: success")
        val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            cameraManager.openCamera(mCameraId!!, stateCallback, cameraHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private var mCameraDevice: CameraDevice? = null
    private val stateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            Log.d(TAG, "onOpen")
            mCameraDevice = cameraDevice
            startPreview(mCameraDevice!!)
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            Log.d(TAG, "onDisconnected")
        }

        override fun onError(cameraDevice: CameraDevice, i: Int) {
            Log.d(TAG, "onError")
        }
    }

    private fun startPreview(mCameraDevice: CameraDevice) {
        try {
            Log.d(TAG, "startPreview")
            setPreviewImageReader()
            previewCaptureRequestBuilder =
                mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            val previewSurfaceTexture = previewTextureView!!.surfaceTexture
            previewSurfaceTexture!!.setDefaultBufferSize(mWidth, mHeight)
            val previewSurface = Surface(previewSurfaceTexture)
            val previewImageReaderSurface = previewImageReader!!.surface
            previewCaptureRequestBuilder!!.addTarget(previewSurface)
            mCameraDevice.createCaptureSession(
                Arrays.asList(
                    previewSurface,
                    previewImageReaderSurface
                ), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        Log.d(TAG, "onConfigured")
                        previewCaptureSession = cameraCaptureSession
                        try {
                            cameraCaptureSession.setRepeatingRequest(
                                previewCaptureRequestBuilder!!.build(),
                                cameraPreviewCallback,
                                cameraHandler
                            )
                        } catch (e: CameraAccessException) {
                            throw RuntimeException(e)
                        }
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {}
                }, cameraHandler
            )
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    @Volatile
    private var videoIsReadyToStop = false
    private val cameraPreviewCallback: CaptureCallback = object : CaptureCallback() {
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)
            if (videoIsReadyToStop) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
                videoIsReadyToStop = false
                stopMediaCodecThread()
            }
        }
    }

    private fun getOptimalSize(sizes: Array<Size>, width: Int, height: Int): Size {
        var tempSize = Size(width, height)
        val adaptSize: MutableList<Size> = ArrayList()
        for (size in sizes) {
            if (width > height) {
                //横屏的时候看，或是平板形式
                if (size.height > height && size.width > width) {
                    adaptSize.add(size)
                }
            } else {
                //竖屏的时候
                if (size.width > height && size.height > width) {
                    adaptSize.add(size)
                }
            }
        }
        if (adaptSize.size > 0) {
            tempSize = adaptSize[0]
            var minnum = 999999
            for (size in adaptSize) {
                val num = size.height * size.height - width * height
                if (num < minnum) {
                    minnum = num
                    tempSize = size
                }
            }
        }
        return tempSize
    }

    override fun onClick(view: View) {
        if (view.id == R.id.shutterclick) {
            if (isRecordingVideo) {
                //stop recording video
                isRecordingVideo = false
                //开始停止录像
                Log.d(TAG, "Stop recording video")
                stopRecordingVideo()
            } else {
                isRecordingVideo = true
                //开始录像
                startRecording()
            }
        }
    }

    private fun stopRecordingVideo() {
        stopVideoSession()
        stopAudioRecord()
    }

    private fun stopMediaMuxer() {
        isMediaMuxerStarted = -1
        mediaMuxer!!.stop()
        mediaMuxer!!.release()
    }

    private fun stopMediaCodecThread() {
        isStop = 2
        AudioCodecThread = null
        VideoCodecThread = null
    }

    private fun stopMediaCodec() {
        AudioCodec!!.stop()
        AudioCodec!!.release()
        videoMediaCodec!!.stop()
        videoMediaCodec!!.release()
    }

    private fun stopVideoSession() {
        if (previewCaptureSession != null) {
            videoIsReadyToStop = try {
                previewCaptureSession!!.stopRepeating()
                true
            } catch (e: CameraAccessException) {
                throw RuntimeException(e)
            }
        }
        try {
            previewCaptureSession!!.setRepeatingRequest(
                previewCaptureRequestBuilder!!.build(),
                cameraPreviewCallback,
                cameraHandler
            )
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    private fun stopAudioRecord() {
        mIsAudioRecording = false
        audioRecord!!.stop()
        audioRecord!!.release()
        audioRecord = null
        audioRecordThread = null
    }

    private fun startRecording() {
        isStop = 1
        nanoTime = System.nanoTime()
        initMediaMuxer()
        initAudioRecord()
        initMediaCodec(mWidth, mHeight)
        initAudioCodec()
        //將MediaCodec分成獨立的線程
        initMediaCodecThread()
        //啟動MediaCodec以及线程
        startMediaCodec()
        //开启录像session
        startVideoSession()
        //音頻開始錄製
        startAudioRecord()
    }

    private fun startMediaCodec() {
        if (AudioCodec != null) {
            AudioCodecIsStoped = 0
            AudioCodec!!.start()
        }
        if (videoMediaCodec != null) {
            videoMediaCodecIsStoped = 0
            videoMediaCodec!!.start()
        }
        if (VideoCodecThread != null) {
            VideoCodecThread!!.start()
        }
        if (AudioCodecThread != null) {
            AudioCodecThread!!.start()
        }
    }

    private fun initMediaCodecThread() {
        VideoCodecThread = Thread { //输出为H264
            while (true) {
                if (isStop == 2) {
                    Log.d(TAG, "videoMediaCodec is stopping")
                    break
                }
                encodeVideoH264()
            }
            videoMediaCodec!!.stop()
            videoMediaCodec!!.release()
            videoMediaCodecIsStoped = 1
            if (AudioCodecIsStoped == 1) {
                stopMediaMuxer()
            }
        }
        AudioCodecThread = Thread {
            while (true) {
                if (isStop == 2) {
                    Log.d(TAG, "AudioCodec is stopping")
                    break
                }
                encodePCMToAC()
            }
            AudioCodec!!.stop()
            AudioCodec!!.release()
            AudioCodecIsStoped = 1
            if (videoMediaCodecIsStoped == 1) {
                stopMediaMuxer()
            }
        }
    }

    private fun startAudioRecord() {
        audioRecordThread = Thread {
            mIsAudioRecording = true
            audioRecord!!.startRecording()
            while (mIsAudioRecording) {
                val inputAudioData = ByteArray(AudioiMinBufferSize)
                val res = audioRecord!!.read(inputAudioData, 0, inputAudioData.size)
                if (res > 0) {
                    //Log.d(TAG,res+"");
                    if (AudioCodec != null) {
                        if (captureListener != null) {
                            captureListener!!.onCaptureListener(inputAudioData, res)
                        }
                        //callbackData(inputAudioData,inputAudioData.length);
                    }
                }
            }
        }
        audioRecordThread!!.start()
    }

    private fun initMediaMuxer() {
        val filename =
            Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Camera/" + currentTime + ".mp4"
        try {
            mediaMuxer = MediaMuxer(filename, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            mediaMuxer!!.setOrientationHint(90)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    val currentTime: String
        get() {
            val date = Date(System.currentTimeMillis())
            val dateFormat: DateFormat = SimpleDateFormat("yyyyMMddhhmmss")
            return dateFormat.format(date)
        }

    //初始化Audio MediaCodec
    private fun initAudioCodec() {
        val format = MediaFormat.createAudioFormat(
            MediaFormat.MIMETYPE_AUDIO_AAC,
            SAMPLE_RATE,
            CHANNEL_COUNT
        )
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 8192)
        try {
            AudioCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
            AudioCodec!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun callbackData(inputAudioData: ByteArray, length: Int) {
        //已经拿到AudioRecord的byte数据
        //准备将其放入到MediaCodc中
        val index = AudioCodec!!.dequeueInputBuffer(-1)
        if (index < 0) {
            return
        }
        Log.d(TAG, "AudioCodec.dequeueInputBuffer:$index")
        val inputBuffers = AudioCodec!!.inputBuffers
        val audioInputBuffer = inputBuffers[index]
        audioInputBuffer.clear()
        Log.d(TAG, "call back Data length:$length")
        Log.d(TAG, "call back Data audioInputBuffer remain:" + audioInputBuffer.remaining())
        audioInputBuffer.put(inputAudioData)
        audioInputBuffer.limit(inputAudioData.size)
        presentationTimeUs += (1.0 * length / (44100 * 2 * (16 / 8)) * 1000000.0).toLong()
        AudioCodec!!.queueInputBuffer(
            index,
            0,
            inputAudioData.size,
            (System.nanoTime() - nanoTime) / 1000,
            0
        )
    }

    /*private void getEncordData() {
        MediaCodec.BufferInfo outputBufferInfo = new MediaCodec.BufferInfo();
        //获得输出
        int flag = AudioCodec.dequeueOutputBuffer(outputBufferInfo, 0);
        Log.d(TAG, "CALL BACK DATA FLAG:" + flag);
        if (flag == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            //第一次都会执行这个
            //这时候可以加轨道到MediaMuxer中，但是先不要启动，等到两个轨道都加好再start
            mAudioTrackIndex = mediaMuxer.addTrack(AudioCodec.getOutputFormat());
            if (mAudioTrackIndex != -1 && mVideoTrackIndex != -1) {
                mediaMuxer.start();
                Log.d(TAG, "AudioMediaCodec start mediaMuxer");
                isMediaMuxerStarted = true;
            }
        } else {
            if (isMediaMuxerStarted) {
                if (flag >= 0) {
                    if (mAudioTrackIndex != -1) {
                        Log.d(TAG, "AudioCodec.getOutputBuffer:");
                        ByteBuffer outputBuffer = AudioCodec.getOutputBuffer(flag);
                        mediaMuxer.writeSampleData(mAudioTrackIndex, outputBuffer, outputBufferInfo);
                        AudioCodec.releaseOutputBuffer(flag, false);
                    }
                }
            }
        }
    }*/
    private fun startVideoSession() {
        Log.d(TAG, "startVideoSession")
        if (previewCaptureSession != null) {
            try {
                previewCaptureSession!!.stopRepeating()
            } catch (e: CameraAccessException) {
                throw RuntimeException(e)
            }
        }
        val previewSurfaceTexture = previewTextureView!!.surfaceTexture
        val previewSurface = Surface(previewSurfaceTexture)
        val previewImageReaderSurface = previewImageReader!!.surface
        try {
            videoRequest = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            videoRequest!!.set(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )
            videoRequest!!.addTarget(previewSurface)
            videoRequest!!.addTarget(previewImageReaderSurface)
            previewCaptureSession!!.setRepeatingRequest(
                videoRequest!!.build(),
                videosessioncallback,
                cameraHandler
            )
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    private val videosessioncallback: CaptureCallback = object : CaptureCallback() {
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)
        }
    }

    interface AudioCaptureListener {
        /**
         * 音频采集回调数据源
         *
         * @param audioSource   ：音频采集回调数据源
         * @param audioReadSize :每次读取数据的大小
         */
        fun onCaptureListener(audioSource: ByteArray, audioReadSize: Int)
    }

    companion object {
        private const val TAG = "VideoRecord"
        private const val WAIT_TIME = 0
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_COUNT = 2
        private const val BIT_RATE = 96000
        private fun YUV_420_888toNV21(image: Image): ByteArray {
            val width = image.width
            val height = image.height
            Log.d(TAG, "image.getWidth():" + image.width)
            Log.d(TAG, "image.getHeight()" + image.height)
            val yBuffer = getBufferWithoutPadding(
                image.planes[0].buffer, image.width, image.planes[0].rowStride, image.height, false
            )
            val vBuffer: ByteBuffer
            //part1 获得真正的消除padding的ybuffer和ubuffer。需要对P格式和SP格式做不同的处理。如果是P格式的话只能逐像素去做，性能会降低。
            vBuffer = if (image.planes[2].pixelStride == 1) { //如果为true，说明是P格式。
                getuvBufferWithoutPaddingP(
                    image.planes[1].buffer, image.planes[2].buffer,
                    width, height, image.planes[1].rowStride, image.planes[1].pixelStride
                )
            } else {
                getBufferWithoutPadding(
                    image.planes[2].buffer,
                    image.width,
                    image.planes[2].rowStride,
                    image.height / 2,
                    true
                )
            }

            //part2 将y数据和uv的交替数据（除去最后一个v值）赋值给nv21
            val ySize = yBuffer.remaining()
            val vSize = vBuffer.remaining()
            val nv21: ByteArray
            val byteSize = width * height * 3 / 2
            nv21 = ByteArray(byteSize)
            yBuffer[nv21, 0, ySize]
            vBuffer[nv21, ySize, vSize]

            //part3 最后一个像素值的u值是缺失的，因此需要从u平面取一下。
            val uPlane = image.planes[1].buffer
            val lastValue = uPlane[uPlane.capacity() - 1]
            nv21[byteSize - 1] = lastValue
            return nv21
        }

        //Semi-Planar格式（SP）的处理和y通道的数据
        private fun getBufferWithoutPadding(
            buffer: ByteBuffer,
            width: Int,
            rowStride: Int,
            times: Int,
            isVbuffer: Boolean
        ): ByteBuffer {
            var width = width
            if (width == rowStride) return buffer //没有buffer,不用处理。
            var bufferPos = buffer.position()
            val cap = buffer.capacity()
            val byteArray = ByteArray(times * width)
            var pos = 0
            //对于y平面，要逐行赋值的次数就是height次。对于uv交替的平面，赋值的次数是height/2次
            for (i in 0 until times) {
                buffer.position(bufferPos)
                //part 1.1 对于u,v通道,会缺失最后一个像u值或者v值，因此需要特殊处理，否则会crash
                if (isVbuffer && i == times - 1) {
                    width = width - 1
                }
                buffer[byteArray, pos, width]
                bufferPos += rowStride
                pos = pos + width
            }

            //nv21数组转成buffer并返回
            val bufferWithoutPaddings = ByteBuffer.allocate(byteArray.size)
            // 数组放到buffer中
            bufferWithoutPaddings.put(byteArray)
            //重置 limit 和postion 值否则 buffer 读取数据不对
            bufferWithoutPaddings.flip()
            return bufferWithoutPaddings
        }

        //Planar格式（P）的处理
        private fun getuvBufferWithoutPaddingP(
            uBuffer: ByteBuffer,
            vBuffer: ByteBuffer,
            width: Int,
            height: Int,
            rowStride: Int,
            pixelStride: Int
        ): ByteBuffer {
            var pos = 0
            val byteArray = ByteArray(height * width / 2)
            for (row in 0 until height / 2) {
                for (col in 0 until width / 2) {
                    val vuPos = col * pixelStride + row * rowStride
                    byteArray[pos++] = vBuffer[vuPos]
                    byteArray[pos++] = uBuffer[vuPos]
                }
            }
            val bufferWithoutPaddings = ByteBuffer.allocate(byteArray.size)
            // 数组放到buffer中
            bufferWithoutPaddings.put(byteArray)
            //重置 limit 和postion 值否则 buffer 读取数据不对
            bufferWithoutPaddings.flip()
            return bufferWithoutPaddings
        }

        private fun YUVToNV21_NV12(image: Image, nv21: ByteArray, w: Int, h: Int, type: String) {
            val planes = image.planes
            val remaining0 = planes[0].buffer.remaining()
            val remaining1 = planes[1].buffer.remaining()
            val remaining2 = planes[2].buffer.remaining()
            //分别准备三个数组接收YUV分量。
            val yRawSrcBytes = ByteArray(remaining0)
            val uRawSrcBytes = ByteArray(remaining1)
            val vRawSrcBytes = ByteArray(remaining2)
            planes[0].buffer[yRawSrcBytes]
            planes[1].buffer[uRawSrcBytes]
            planes[2].buffer[vRawSrcBytes]
            var j = 0
            var k = 0
            var flag = type == "NV21"
            for (i in nv21.indices) {
                if (i < w * h) {
                    //首先填充w*h个Y分量
                    nv21[i] = yRawSrcBytes[i]
                } else {
                    if (flag) {
                        //若NV21类型 则Y分量分配完后第一个将是V分量
                        nv21[i] = vRawSrcBytes[j]
                        //PixelStride有用数据步长 = 1紧凑按顺序填充，=2每间隔一个填充数据
                        j += planes[1].pixelStride
                    } else {
                        //若NV12类型 则Y分量分配完后第一个将是U分量
                        nv21[i] = uRawSrcBytes[k]
                        //PixelStride有用数据步长 = 1紧凑按顺序填充，=2每间隔一个填充数据
                        k += planes[2].pixelStride
                    }
                    //紧接着可以交错UV或者VU排列不停的改变flag标志即可交错排列
                    flag = !flag
                }
            }
        }
    }
}