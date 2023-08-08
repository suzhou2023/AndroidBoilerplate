package com.bbt2000.boilerplate.demos.gles._02_camera.encode

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.Surface


/**
 *  author : sz
 *  date : 2023/8/8
 *  description :
 */
class VideoEncoder(width: Int, height: Int, encodeHandler: Handler) {
    private val mWidth: Int = width
    private val mHeight: Int = height
    private var mEncodeHandler: Handler = encodeHandler

    private var mVideoCodec: MediaCodec? = null
    private var mMp4Muxer: Mp4Muxer? = null


    private var mInputSurface: Surface? = null

    private var mConfigured: Boolean = false
    private var mEncodeState: EncodeState = EncodeState.STOPPED
    private var mStateCallback: ((state: EncodeState) -> Unit)? = null
    private var mPausing: Boolean = false // 暂停录制中（为了暂停恢复不出现花屏和跳屏，需要等待关键帧）
    private var mResuming: Boolean = false // 恢复录制中（恢复的时候需要从第一个关键帧开始录制）

    init {
        try {
            mVideoCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        } catch (e: Exception) {
            Log.e(TAG, "VideoCodec create failed.", e)
            e.printStackTrace()
        }
    }

    fun setMuxer(mp4Muxer: Mp4Muxer) {
        mMp4Muxer = mp4Muxer
    }

    fun getEncodeState(): EncodeState = mEncodeState

    fun setStateCallback(callback: (state: EncodeState) -> Unit) {
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
                    mMp4Muxer?.writeSampleData(buffer, info, true, resumedFromPause)
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
                Log.e(TAG, "Release output buffer error.", e)
                e.printStackTrace()
            }
        }

        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            Log.e(TAG, "Codec error.", e)
        }

        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
            Log.i(TAG, "onOutputFormatChanged: $format")
            // 在这个回调以外的地方启动muxer后面stop muxer的时候会抛异常
            // 应该是格式设置不正确导致的
            mMp4Muxer?.addTrackAndStart(videoFormat = codec.outputFormat)
        }
    }

    private fun configure() {
        try {
            if (mConfigured) return
            val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight)
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 8_000_000)
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
            // 6.0以后显示设置回调执行线程
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mVideoCodec?.setCallback(mCodecCallback, mEncodeHandler)
            } else {
                mVideoCodec?.setCallback(mCodecCallback)
            }
            mVideoCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mConfigured = true
            Log.i(TAG, "VideoCodec configure success.")
        } catch (e: Exception) {
            Log.e(TAG, "VideoCodec configure failed.", e)
            e.printStackTrace()
        }
    }

    fun start(callback: ((surface: Surface) -> Unit)?): Boolean {
        try {
            configure()
            if (!mConfigured) return false
            // 视频编码surface,需在start之前创建
            mInputSurface = mVideoCodec?.createInputSurface()
            callback?.invoke(mInputSurface!!)
            // 启动视频编码codec
            mVideoCodec?.start()
            mEncodeState = EncodeState.STARTED
            mStateCallback?.invoke(mEncodeState)
            Log.i(TAG, "VideoCodec started.")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "VideoCodec start failed.", e)
            e.printStackTrace()
        }
        return false
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
            mConfigured = false
            mVideoCodec?.signalEndOfInputStream()
            mVideoCodec?.stop()
            mInputSurface?.release()
            mInputSurface = null
            mMp4Muxer?.stop()
            mEncodeState = EncodeState.STOPPED
            mStateCallback?.invoke(mEncodeState)
            Log.i(TAG, "VideoCodec stopped.")
        } catch (e: Exception) {
            Log.e(TAG, "VideoCodec stop failed.", e)
            e.printStackTrace()
        }
    }

    fun release() {
        mVideoCodec?.release()
        mVideoCodec = null
    }

    companion object {
        private const val TAG = "VideoEncoder"
    }
}




















