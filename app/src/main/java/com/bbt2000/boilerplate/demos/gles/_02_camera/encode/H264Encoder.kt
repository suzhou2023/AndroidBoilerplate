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

    private val mCodecCallback = object : MediaCodec.Callback() {
        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {}
        override fun onOutputBufferAvailable(
            codec: MediaCodec,
            index: Int,
            info: MediaCodec.BufferInfo
        ) {
            try {
                val buffer = codec.getOutputBuffer(index) ?: return
                if (!mMp4Muxer.isStarted()) {
                    codec.releaseOutputBuffer(index, false)
                    return
                }
                mMp4Muxer.writeSampleData(buffer, info, true)
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

    init {
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
            val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight)
            // todo: try different frame rate
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, getEncodeBitrate(mWidth, mHeight))
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL)
            // todo: 这个会导致配置失败
//            mediaFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileHigh)
            mediaFormat.setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
            )
            mMediaCodec?.setCallback(mCodecCallback)
            mMediaCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mInputSurface = mMediaCodec?.createInputSurface()
            mConfigured = true
            Log.i(TAG, "Mediacodec configure success.")
        } catch (e: Exception) {
            Log.e(TAG, "Mediacodec configure failed.", e)
            e.printStackTrace()
        }
    }

    fun getSurface() = mInputSurface

    fun getEncodeState(): EncodeState {
        return mEncodeState
    }

    fun start() {
        mEncodeHandler.post {
            try {
                if (!mConfigured) return@post
                mMediaCodec?.start()
                mEncodeState = EncodeState.STARTED
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
            Log.i(TAG, "Mediacodec paused.")
        }
    }

    fun resume() {
        mEncodeHandler.post {
            if (!mConfigured) return@post
            mEncodeState = EncodeState.STARTED
            Log.i(TAG, "Mediacodec resumed.")
        }
    }

    fun stop() {
        mEncodeHandler.post {
            try {
                if (!mConfigured) return@post
                mMediaCodec?.signalEndOfInputStream()
                mMediaCodec?.stop()
                mMp4Muxer.stop()
                mEncodeState = EncodeState.INIT
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
        mInputSurface?.release()
        mInputSurface = null
        mEncodeThread.quitSafely()
    }

    private fun getEncodeBitrate(width: Int, height: Int): Int {
        var bitRate = width * height * 20 * 3 * 0.07F
        if (width >= 1920 || height >= 1920) {
            bitRate *= 0.75F
        } else if (width >= 1280 || height >= 1280) {
            bitRate *= 1.2F
        } else if (width >= 640 || height >= 640) {
            bitRate *= 1.4F
        }
        return bitRate.toInt()
    }

    companion object {
        private const val TAG = "H264Encoder"
        private const val FRAME_RATE = 30
        private const val I_FRAME_INTERVAL = 1
    }
}




















