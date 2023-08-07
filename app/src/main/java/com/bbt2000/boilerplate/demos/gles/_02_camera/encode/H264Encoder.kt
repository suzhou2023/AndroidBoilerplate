package com.bbt2000.boilerplate.demos.gles._02_camera.encode

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface

enum class EncodeState {
    INIT, STARTED, PAUSED
}

class H264Encoder(width: Int, height: Int) {
    private var mMediaCodec: MediaCodec? = null
    private val mMp4Muxer: Mp4Muxer by lazy { Mp4Muxer() }
    private val mWidth: Int = width
    private val mHeight: Int = height
    private val mEncodeThread: HandlerThread by lazy { HandlerThread("encode").apply { start() } }
    private var mEncodeHandler: Handler = Handler(mEncodeThread.looper)
    private var mInputSurface: Surface? = null
    private var mConfigured: Boolean = false
    private var mEncodeState: EncodeState = EncodeState.INIT
    private var mStateCallback: ((state: EncodeState) -> Unit)? = null
    private var mResumeFromPause: Boolean = false // 是否从暂停恢复

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
                if (mEncodeState == EncodeState.STARTED && mMp4Muxer.started()) {
                    mMp4Muxer.writeSampleData(buffer, info, true, mResumeFromPause)
                    if (mResumeFromPause) mResumeFromPause = false
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
            mMediaCodec?.setCallback(mCodecCallback)
            mMediaCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mConfigured = true
            Log.i(TAG, "Mediacodec configure success.")
        } catch (e: Exception) {
            Log.e(TAG, "Mediacodec configure failed.", e)
            e.printStackTrace()
        }
    }

    fun start(callback: (surface: Surface) -> Unit) {
        mEncodeHandler.post {
            try {
                configure()
                if (!mConfigured) return@post
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
    }

    fun pause() {
        mEncodeHandler.post {
            if (!mConfigured) return@post
            mEncodeState = EncodeState.PAUSED
            mStateCallback?.invoke(mEncodeState)
            Log.i(TAG, "Encoder paused.")
        }
    }

    fun resume() {
        mEncodeHandler.post {
            if (!mConfigured) return@post
            mEncodeState = EncodeState.STARTED
            mResumeFromPause = true
            mStateCallback?.invoke(mEncodeState)
            Log.i(TAG, "Encoder resumed.")
        }
    }

    fun stop() {
        mEncodeHandler.post {
            try {
                if (!mConfigured) return@post
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




















