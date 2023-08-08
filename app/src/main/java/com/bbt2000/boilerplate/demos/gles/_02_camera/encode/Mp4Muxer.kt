package com.bbt2000.boilerplate.demos.gles._02_camera.encode

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaMuxer.OutputFormat
import android.util.Log
import com.bbt2000.boilerplate.util.FileUtil
import java.nio.ByteBuffer


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
    private var mVideoPTS: Long = 0L // 视频时间戳，相对于第一帧的时间戳
    private var mVideoPTSBegin: Long = 0L // 视频第一帧的绝对时间戳
    private var mAudioPTS: Long = 0L
    private var mStarted: Boolean = false

    fun started() = mStarted

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
            mStarted = true
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
            mVideoPTS = 0L
            mAudioPTS = 0L
            mStarted = false
            Log.i(TAG, "Muxer stopped.")
        } catch (e: Exception) {
            Log.e(TAG, "Muxer stop failed", e)
            e.printStackTrace()
        }
    }

    fun writeSampleData(
        buffer: ByteBuffer,
        bufferInfo: MediaCodec.BufferInfo,
        isVideo: Boolean,
        resumedFromPause: Boolean = false
    ) {
        if (!mStarted) return
        if (isVideo && mVideoTrackIndex >= 0) {
            // 记录第一帧的绝对时间戳
            if (mVideoPTSBegin == 0L) {
                mVideoPTSBegin = bufferInfo.presentationTimeUs
            }
            // 如果是从暂停恢复，需要调整第一帧的绝对时间戳
            if (resumedFromPause) {
                mVideoPTSBegin += bufferInfo.presentationTimeUs - mVideoPTSBegin - mVideoPTS - 33_000
            }
            // 相对于第一帧的时间戳
            mVideoPTS = bufferInfo.presentationTimeUs - mVideoPTSBegin
            bufferInfo.presentationTimeUs = mVideoPTS
            mMediaMuxer?.writeSampleData(mVideoTrackIndex, buffer, bufferInfo)
        }
        if (!isVideo && mAudioTrackIndex >= 0) {
            mMediaMuxer?.writeSampleData(mAudioTrackIndex, buffer, bufferInfo)
        }
    }

    companion object {
        const val TAG = "Mp4Muxer"
    }
}