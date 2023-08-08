package com.bbt2000.boilerplate.demos.gles._02_camera.encode

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface

enum class EncodeState {
    INIT, STARTED, PAUSED
}

class H264Encoder(width: Int, height: Int) {
    private val mWidth: Int = width
    private val mHeight: Int = height

    private var mMediaCodec: MediaCodec? = null
    private val mMp4Muxer: Mp4Muxer by lazy { Mp4Muxer() }

    private val mEncodeThread: HandlerThread by lazy { HandlerThread("encode").apply { start() } }
    private val mEncodeHandler: Handler by lazy { Handler(mEncodeThread.looper) }
    private var mInputSurface: Surface? = null

    private var mConfigured: Boolean = false
    private var mEncodeState: EncodeState = EncodeState.INIT
    private var mStateCallback: ((state: EncodeState) -> Unit)? = null
    private var mPausing: Boolean = false // 暂停录制中（为了暂停恢复不出现花屏和跳屏，需要等待关键帧）
    private var mResuming: Boolean = false // 恢复录制中（恢复的时候需要从第一个关键帧开始录制）

    init {
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        } catch (e: Exception) {
            Log.e(TAG, "Mediacodec create failed.", e)
            e.printStackTrace()
        }
    }

    fun getEncodeState(): EncodeState = mEncodeState

    fun setEncodeStateCallback(callback: (state: EncodeState) -> Unit) {
        mStateCallback = callback
    }

    private val mCodecCallback = object : MediaCodec.Callback() {
        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {}
        override fun onOutputBufferAvailable(
            codec: MediaCodec,
            index: Int,
            info: MediaCodec.BufferInfo
        ) {
            try {
                val buffer = codec.getOutputBuffer(index) ?: return
                var resumedFromPause = false
                // 恢复录制过程中，等待第一个关键帧
                if (mResuming && (info.flags and MediaCodec.BUFFER_FLAG_KEY_FRAME != 0)) {
                    mEncodeState = EncodeState.STARTED
                    mStateCallback?.invoke(mEncodeState)
                    mResuming = false
                    resumedFromPause = true
                    Log.i(TAG, "Encoder resumed.")
                }
                if (mEncodeState == EncodeState.STARTED) {
                    mMp4Muxer.writeSampleData(buffer, info, true, resumedFromPause)
                    // 暂停录制时，等待一个关键帧以后再暂停
                    if (mPausing && (info.flags and MediaCodec.BUFFER_FLAG_KEY_FRAME != 0)) {
                        mEncodeState = EncodeState.PAUSED
                        mStateCallback?.invoke(mEncodeState)
                        mPausing = false
                        Log.i(TAG, "Encoder paused.")
                    }
                }
                codec.releaseOutputBuffer(index, false)
            } catch (e: Exception) {
                Log.e(TAG, "Consume output buffer error: ${e.message}")
                e.printStackTrace()
            }
        }

        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            Log.e(TAG, "Codec error: ${e.message}")
        }

        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
            Log.i(TAG, "onOutputFormatChanged: $format")
            // 在这个回调以外的地方启动muxer后面stop muxer的时候会抛异常
            // 应该是格式设置不正确导致的
            mMp4Muxer.start(
                videoFormat = codec.outputFormat,
                audioFormat = null
            )
        }
    }

    private fun configure() {
        try {
            val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight)
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 8_000_000)
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
            // 6.0以后显示设置回调执行线程
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mMediaCodec?.setCallback(mCodecCallback, mEncodeHandler)
            } else {
                mMediaCodec?.setCallback(mCodecCallback)
            }
            mMediaCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mConfigured = true
            Log.i(TAG, "Mediacodec configure success.")
        } catch (e: Exception) {
            Log.e(TAG, "Mediacodec configure failed.", e)
            e.printStackTrace()
        }
    }

    fun start(callback: (surface: Surface) -> Unit) {
        try {
            configure()
            if (!mConfigured) return
            mInputSurface = mMediaCodec?.createInputSurface()
            callback(mInputSurface!!)
            mMediaCodec?.start()
            mEncodeState = EncodeState.STARTED
            mStateCallback?.invoke(mEncodeState)
            Log.i(TAG, "Mediacodec started.")
        } catch (e: Exception) {
            Log.e(TAG, "Start mediacodec failed.", e)
            e.printStackTrace()
        }
    }

    fun pause() {
        if (!mConfigured) return
        mPausing = true
        Log.i(TAG, "Encoder pausing.")
    }

    fun resume() {
        if (!mConfigured) return
        mResuming = true
        Log.i(TAG, "Encoder resuming.")
    }

    fun stop() {
        try {
            if (!mConfigured) return
            mMediaCodec?.signalEndOfInputStream()
            mMediaCodec?.stop()
            mInputSurface?.release()
            mInputSurface = null
            mMp4Muxer.stop()
            mEncodeState = EncodeState.INIT
            mStateCallback?.invoke(mEncodeState)
            Log.i(TAG, "Mediacodec stopped.")
        } catch (e: Exception) {
            Log.e(TAG, "Stop mediacodec failed.", e)
            e.printStackTrace()
        }
    }

    fun release() {
        mMediaCodec?.release()
        mMediaCodec = null
        mEncodeThread.quitSafely()
    }

    companion object {
        private const val TAG = "H264Encoder"
    }
}




















