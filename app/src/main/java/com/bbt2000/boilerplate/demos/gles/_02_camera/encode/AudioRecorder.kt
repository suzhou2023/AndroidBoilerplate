package com.bbt2000.boilerplate.demos.gles._02_camera.encode

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaCodec
import android.media.MediaRecorder
import android.util.Log
import java.util.concurrent.ConcurrentLinkedQueue

/**
 *  author : sz
 *  date : 2023/8/8
 *  description :
 */
@SuppressLint("MissingPermission")
class AudioRecorder {
    private var mAudioRecord: AudioRecord? = null
    private var mState: State = State.STOPPED
    private val mDataQueue: ConcurrentLinkedQueue<RawData> by lazy { ConcurrentLinkedQueue() }
    private val mMinBufferSize: Int by lazy {
        AudioRecord.getMinBufferSize(
            44_100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_8BIT
        )
    }

    init {
        try {
            mAudioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                44_100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                mMinBufferSize
            )
        } catch (e: Exception) {
            Log.e(TAG, "AudioRecorder init failed.", e)
            e.printStackTrace()
        }
    }

    fun getDataQueue() = mDataQueue

    fun start() {
        mState = State.STARTED
        Thread {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO)
            try {
                val byteArray = ByteArray(mMinBufferSize)
                mAudioRecord?.startRecording()
                while (mState == State.STARTED) {
                    val bytesOrCode = mAudioRecord?.read(byteArray, 0, mMinBufferSize) ?: continue
                    if (bytesOrCode < 0) {
                        Log.e(TAG, "AudioRecord read error: $bytesOrCode")
                        continue
                    }
                    var rawData: RawData?
                    if (mDataQueue.size >= 5) {
                        rawData = mDataQueue.poll()
                        byteArray.copyInto(rawData.byteArray)
                    } else {
                        rawData = RawData(ByteArray(mMinBufferSize), 0)
                        byteArray.copyInto(rawData.byteArray)
                    }
                    mDataQueue.offer(rawData)
                }
                mDataQueue.offer(RawData(ByteArray(0), MediaCodec.BUFFER_FLAG_END_OF_STREAM))
                mAudioRecord?.stop()
            } catch (e: Exception) {
                Log.e(TAG, "Audio record error.", e)
                e.printStackTrace()
            }
        }.start()
    }

    fun stop() {
        mState = State.STOPPED
    }

    fun release() {
        try {
            mAudioRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "AudioRecorder release failed.", e)
            e.printStackTrace()
        }
    }

    data class RawData(val byteArray: ByteArray, val flags: Int)

    enum class State { STARTED, STOPPED }

    companion object {
        const val TAG = "AudioRecorder"
    }
}










