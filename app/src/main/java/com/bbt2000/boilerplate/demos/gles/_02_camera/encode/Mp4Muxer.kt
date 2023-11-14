package com.bbt2000.boilerplate.demos.gles._02_camera.encode

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaMuxer.OutputFormat
import android.util.Log
import com.bbt2000.boilerplate.common.util.FileUtil
import java.nio.ByteBuffer


/**
 *  author : sz
 *  date : 2023/7/28
 *  description :
 */
class Mp4Muxer(path: String? = null, videoEnabled: Boolean = false, audioEnabled: Boolean = false) {
    private var mPath: String? = path
    private val mVideoEnabled: Boolean = videoEnabled
    private val mAudioEnabled: Boolean = audioEnabled
    private var mMediaMuxer: MediaMuxer? = null

    // 注意添加成功的track index可以为0
    private var mVideoTrackIndex: Int = -1
    private var mAudioTrackIndex: Int = -1
    private var mVideoPTS: Long = 0L // 视频时间戳，相对于第一帧的时间戳
    private var mVideoPTSBegin: Long = 0L // 视频第一帧的绝对时间戳
    private var mAudioPTS: Long = 0L
    private var mAudioPTSBegin: Long = 0L
    private var mStarted: Boolean = false

    init {
        try {
            val file = FileUtil.createRecordFile(mPath)
            if (file != null) {
                mMediaMuxer = MediaMuxer(file.absolutePath, OutputFormat.MUXER_OUTPUT_MPEG_4)
            } else {
                Log.e(TAG, "Muxer init failed.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Muxer init failed.", e)
            e.printStackTrace()
            mMediaMuxer?.release()
        }
    }

    fun addTrackAndStart(videoFormat: MediaFormat? = null, audioFormat: MediaFormat? = null) {
        try {
            if (mVideoEnabled && videoFormat != null) {
                mVideoTrackIndex = mMediaMuxer?.addTrack(videoFormat) ?: return
            }
            if (mAudioEnabled && audioFormat != null) {
                mAudioTrackIndex = mMediaMuxer?.addTrack(audioFormat) ?: return
            }
            if (mVideoEnabled && mVideoTrackIndex < 0) return
            if (mAudioEnabled && mAudioTrackIndex < 0) return

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
            if (mMediaMuxer == null) return
            mMediaMuxer?.stop()
            mMediaMuxer?.release()
            mMediaMuxer = null
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
            if (mAudioPTSBegin == 0L) {
                mAudioPTSBegin = bufferInfo.presentationTimeUs
            }
            if (resumedFromPause) {
                mAudioPTSBegin += bufferInfo.presentationTimeUs - mAudioPTSBegin - mAudioPTS - 33_000
            }
            mAudioPTS = bufferInfo.presentationTimeUs - mAudioPTSBegin
            bufferInfo.presentationTimeUs = mAudioPTS
            mMediaMuxer?.writeSampleData(mAudioTrackIndex, buffer, bufferInfo)
        }
    }

    companion object {
        const val TAG = "Mp4Muxer"
    }
}