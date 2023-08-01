package com.bbt2000.boilerplate.demos.gles._02_camera.encode

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaMuxer.OutputFormat
import android.util.Log
import com.bbt2000.boilerplate.util.FileUtil
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean


/**
 *  author : sz
 *  date : 2023/7/28
 *  description :
 */
class Mp4Muxer {
    private var mMediaMuxer: MediaMuxer? = null

    // 注意添加成功的track index可以为0
    private var mVideoTrackIndex: Int = -1
    private var mAudioTrackIndex: Int = -1
    private var mVideoPts: Long = 0L
    private var mAudioPts: Long = 0L
    private val mStarted: AtomicBoolean by lazy { AtomicBoolean(false) }


    fun isStarted() = mStarted.get()

    fun start(path: String? = null, videoFormat: MediaFormat?, audioFormat: MediaFormat?) {
        try {
            // 至少要有一个track
            if (videoFormat == null && audioFormat == null) return
            val file = FileUtil.createRecordFile(path) ?: return

            mMediaMuxer = MediaMuxer(file.absolutePath, OutputFormat.MUXER_OUTPUT_MPEG_4)
            if (videoFormat != null) {
                mVideoTrackIndex = mMediaMuxer?.addTrack(videoFormat) ?: return
            }
            if (audioFormat != null) {
                mAudioTrackIndex = mMediaMuxer?.addTrack(audioFormat) ?: return
            }
            mMediaMuxer?.start()
            mStarted.set(true)
            Log.i(TAG, "Muxer started.")
        } catch (e: Exception) {
            Log.e(TAG, "Muxer start failed.", e)
            e.printStackTrace()
            mMediaMuxer?.release()
        }
    }

    fun stop() {
        try {
            mMediaMuxer?.stop()
            mMediaMuxer?.release()
            mVideoTrackIndex = -1
            mAudioTrackIndex = -1
            mVideoPts = 0L
            mAudioPts = 0L
            mStarted.set(false)
            Log.i(TAG, "Muxer stopped.")
        } catch (e: Exception) {
            Log.e(TAG, "Muxer stop failed", e)
            e.printStackTrace()
        }
    }

    fun writeSampleData(buffer: ByteBuffer, bufferInfo: MediaCodec.BufferInfo, isVideo: Boolean) {
        if (!mStarted.get()) return
        if (bufferInfo.size <= 0) return
        buffer.position(bufferInfo.offset)
        buffer.limit(bufferInfo.offset + bufferInfo.size)
        if (isVideo && mVideoTrackIndex >= 0) {
            if (mVideoPts == 0L) {
                mVideoPts = bufferInfo.presentationTimeUs
            }
            bufferInfo.presentationTimeUs = bufferInfo.presentationTimeUs - mVideoPts
            Log.d(TAG, "size = ${bufferInfo.size}")
            Log.d(TAG, "presentationTimeUs = ${bufferInfo.presentationTimeUs}")
            mMediaMuxer?.writeSampleData(mVideoTrackIndex, buffer, bufferInfo)
        }
        if (!isVideo && mAudioTrackIndex >= 0) {
            if (mAudioPts == 0L) {
                mAudioPts = bufferInfo.presentationTimeUs
            }
            bufferInfo.presentationTimeUs = bufferInfo.presentationTimeUs - mAudioPts
            mMediaMuxer?.writeSampleData(mAudioTrackIndex, buffer, bufferInfo)
        }
    }

    companion object {
        const val TAG = "Mp4Muxer"
    }
}