package com.bbt2000.boilerplate.demos.gles._02_camera.encode

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import java.util.concurrent.atomic.AtomicBoolean

enum class EncodeState {
    INIT, STARTED, PAUSED
}

class H264Encoder(width: Int, height: Int) {
    private val mWidth: Int = width
    private val mHeight: Int = height
    private val mEncodeThread: HandlerThread by lazy { HandlerThread("encode").apply { start() } }
    private var mEncodeHandler: Handler = Handler(mEncodeThread.looper)
    private lateinit var mMediaCodec: MediaCodec
    private var mSurface: Surface? = null
    private val mConfigured: AtomicBoolean by lazy { AtomicBoolean(false) }
    private val mMp4Muxer: Mp4Muxer by lazy { Mp4Muxer() }

    // todo: 多线程访问问题
    private var mEncodeState: EncodeState = EncodeState.INIT
    private var mCallback: Callback? = null

    fun getEncodeState(): EncodeState {
        return mEncodeState
    }

    fun setCallback(callback: Callback) {
        mCallback = callback
    }

    interface Callback {
        fun onSurfaceAvailable(surface: Surface)
        fun onConfigured()
    }

    private val mCodecCallback = object : MediaCodec.Callback() {
        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {}
        override fun onOutputBufferAvailable(
            codec: MediaCodec,
            index: Int,
            info: MediaCodec.BufferInfo
        ) {
            try {
                Log.i(TAG, "onOutputBufferAvailable")
                val buffer = mMediaCodec.getOutputBuffer(index) ?: return
                if (!mMp4Muxer.isStarted()) {
                    codec.releaseOutputBuffer(index, false)
                    return
                }
//                mMp4Muxer.writeSampleData(buffer, info, true)
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
            Log.i(TAG, "Codec output format changed: $format")
        }
    }

    fun configure() {
        mEncodeHandler.post {
            try {
                mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
                val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight)
                // todo: try different frame rate
                mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
                mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, getEncodeBitrate(mWidth, mHeight))
                mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL)
                // todo: 这个会导致配置失败
//                mediaFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileHigh)
                mediaFormat.setInteger(
                    MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
                )
                mMediaCodec.setCallback(mCodecCallback)
                mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                mSurface = mMediaCodec.createInputSurface()
                mCallback?.onSurfaceAvailable(mSurface!!)
                mCallback?.onConfigured()
                mConfigured.set(true)
                Log.i(TAG, "Configure encoder success.")
            } catch (e: Exception) {
                Log.e(TAG, "Configure encoder failed: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun getSupportColorFormat(): Int {
//        return MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar
        return MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
    }

    fun start() {
        mEncodeHandler.post {
            try {
                if (!mConfigured.get()) return@post
                mMediaCodec.start()
                mMp4Muxer.start(
                    videoFormat = mMediaCodec.outputFormat,
                    audioFormat = null
                )
                mEncodeState = EncodeState.STARTED
                Log.i(TAG, "Start encoder success.")
            } catch (e: Exception) {
                Log.e(TAG, "Start encoder failed: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun pause() {
        mEncodeHandler.post {
            mEncodeState = EncodeState.PAUSED
            Log.i(TAG, "Encoder paused.")
        }
    }

    fun resume() {
        mEncodeHandler.post {
            mEncodeState = EncodeState.STARTED
            Log.i(TAG, "Encoder resumed.")
        }
    }

    fun stop() {
        mEncodeHandler.post {
            try {
                mMediaCodec.signalEndOfInputStream()
                mMediaCodec.stop()
                // todo
//                mEncodeHandler.postDelayed({ mMp4Muxer.stop() }, 2000)
                mEncodeState = EncodeState.INIT
                Log.i(TAG, "Stop encoder success.")
            } catch (e: Exception) {
                Log.e(TAG, "Stop encoder failed: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun release() {
        mMediaCodec.release()
        mSurface?.release()
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

    fun createSharedContext(eglConfigInfo: Long) {
        nativeCreateSharedContext(eglConfigInfo)
    }

    private external fun nativeCreateSharedContext(eglConfigInfo: Long)

    companion object {
        private const val TAG = "H264Encoder"
        private const val FRAME_RATE = 30
        private const val I_FRAME_INTERVAL = 1
    }
}