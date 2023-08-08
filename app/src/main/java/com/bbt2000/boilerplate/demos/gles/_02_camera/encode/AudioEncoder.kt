package com.bbt2000.boilerplate.demos.gles._02_camera.encode

import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Build
import android.os.Handler
import android.util.Log
import java.nio.ByteBuffer


/**
 *  author : sz
 *  date : 2023/8/8
 *  description :
 */
class AudioEncoder(encodeHandler: Handler) {
    private var mEncodeHandler: Handler = encodeHandler
    private var mAudioCodec: MediaCodec? = null
    private var mAudioRecorder: AudioRecorder? = null
    private var mMp4Muxer: Mp4Muxer? = null
    private var mConfigured: Boolean = false
    private var mEncodeState: EncodeState = EncodeState.STOPPED
    private var mStateCallback: ((state: EncodeState) -> Unit)? = null

    init {
        try {
            mAudioCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
            mAudioRecorder = AudioRecorder()
        } catch (e: Exception) {
            Log.e(TAG, "AudioCodec create failed.", e)
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
        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
            try {
                val buffer = codec.getInputBuffer(index)
                val queue = mAudioRecorder?.getDataQueue()
                val rawData = queue?.poll()
                if (rawData != null) {
                    if ((rawData.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {

                    }
                    buffer?.put(rawData.byteArray)
                    buffer?.position(0)
                    buffer?.limit(rawData.byteArray.size)
                    codec.queueInputBuffer(index, 0, rawData.byteArray.size, System.nanoTime() / 1000, 0)
                } else {
                    codec.queueInputBuffer(index, 0, 0, System.nanoTime() / 1000, 0)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Queue input buffer error.", e)
                e.printStackTrace()
            }
        }

        override fun onOutputBufferAvailable(
            codec: MediaCodec,
            index: Int,
            info: MediaCodec.BufferInfo
        ) {
            try {
                Log.d(TAG, "================onOutputBufferAvailable: ${info.size}")
                val buffer = codec.getOutputBuffer(index) ?: return
                if (mEncodeState == EncodeState.STARTED) {
                    val byteArray = ByteArray(info.size + 7)
                    addADTStoPacket(byteArray, byteArray.size)
                    buffer.position(info.offset)
                    buffer.limit(info.offset + info.size)
                    buffer.get(byteArray, 7, info.size)

                    val byteBuffer = ByteBuffer.allocate(byteArray.size)
                    byteBuffer.put(byteArray)
                    info.offset = 0
                    info.size = byteArray.size
                    info.presentationTimeUs = System.nanoTime() / 1000
                    info.flags = 0
                    mMp4Muxer?.writeSampleData(byteBuffer, info, false)
                }
                codec.releaseOutputBuffer(index, false)
            } catch (e: Exception) {
                Log.e(TAG, "Release output buffer error.", e)
                e.printStackTrace()
            }
        }

        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            Log.e(TAG, "On error.", e)
        }

        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
            Log.i(TAG, "onOutputFormatChanged: $format")
            // 在这个回调以外的地方启动muxer后面stop muxer的时候会抛异常
            // 应该是格式设置不正确导致的
            mMp4Muxer?.addTrackAndStart(audioFormat = codec.outputFormat)
        }
    }

    private fun configure() {
        try {
            if (mConfigured) return
            val mediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 44_100, 1)
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96_000)
            // 6.0以后显示设置回调执行线程
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAudioCodec?.setCallback(mCodecCallback, mEncodeHandler)
            } else {
                mAudioCodec?.setCallback(mCodecCallback)
            }
            mAudioCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mConfigured = true
            Log.i(TAG, "AudioCodec configure success.")
        } catch (e: Exception) {
            Log.e(TAG, "AudioCodec configure failed.", e)
            e.printStackTrace()
        }
    }

    fun start(): Boolean {
        try {
            configure()
            if (!mConfigured) return false
            // 启动录音
            mAudioRecorder?.start()
            // 启动音频编码codec
            mAudioCodec?.start()
            mEncodeState = EncodeState.STARTED
            mStateCallback?.invoke(mEncodeState)
            Log.i(TAG, "AudioCodec started.")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "AudioCodec start failed.", e)
            e.printStackTrace()
        }
        return false
    }

    fun pause() {
        if (!mConfigured) return
        mEncodeState = EncodeState.PAUSED
        mStateCallback?.invoke(mEncodeState)
        Log.i(TAG, "Encoder paused.")
    }

    fun resume() {
        if (!mConfigured) return
        mEncodeState = EncodeState.STARTED
        mStateCallback?.invoke(mEncodeState)
        Log.i(TAG, "Encoder resumed.")
    }

    fun stop() {
        try {
            if (!mConfigured) return
            mConfigured = false
            mAudioCodec?.stop()
            mAudioRecorder?.stop()
            mMp4Muxer?.stop()
            mEncodeState = EncodeState.STOPPED
            mStateCallback?.invoke(mEncodeState)
            Log.i(TAG, "AudioCodec stopped.")
        } catch (e: Exception) {
            Log.e(TAG, "AudioCodec stop failed.", e)
            e.printStackTrace()
        }
    }

    fun release() {
        mAudioCodec?.release()
        mAudioCodec = null
        mAudioRecorder?.release()
    }

    /**
     *  Add ADTS header at the beginning of each and every AAC packet.
     *  This is needed as MediaCodec encoder generates a packet of raw
     *  AAC data.
     */
    private fun addADTStoPacket(packet: ByteArray, packetLen: Int) {
        val profile = 2 // AAC LC
        val freqIdx = 4 // 44.1KHz
        val chanCfg = 2 // CPE

        packet[0] = 0xff.toByte()
        packet[1] = 0xf9.toByte()
        packet[2] = ((profile - 1 shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
        packet[3] = ((chanCfg and 3 shl 6) + (packetLen shr 11)).toByte()
        packet[4] = (packetLen and 0x7ff shr 3).toByte()
        packet[5] = ((packetLen and 7 shl 5) + 0x1f).toByte()
        packet[6] = 0xfc.toByte()
    }

    companion object {
        private const val TAG = "AudioEncoder"
    }
}




















