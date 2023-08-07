package com.bbt2000.boilerplate.demos.gles._02_camera.encode

import android.media.MediaCodecList
import android.media.MediaFormat
import android.util.Log


/**
 *  author : suzhou
 *  date : 2023/8/4 21:27
 *  description :
 */
object CodecUtil {
    const val TAG = "CodecUtil"
    fun getCodecInfos() {
        val codecInfo = MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos
            .find { it.isEncoder && it.supportedTypes.contains(MediaFormat.MIMETYPE_VIDEO_AVC) }
        codecInfo ?: return
        val capabilities = codecInfo.getCapabilitiesForType(MediaFormat.MIMETYPE_VIDEO_AVC)
        Log.d(TAG, "mimeType=${capabilities.mimeType}")
        for (format in capabilities.colorFormats) {
            Log.d(TAG, "colorFormats=${format.toString(10)}")
        }
        Log.d(TAG, "colorFormats=${capabilities.colorFormats.contentToString()}")
        Log.d(TAG, "defaultFormat=${capabilities.defaultFormat}")
        Log.d(TAG, "profileLevels=${capabilities.profileLevels}")
        val encoderCapabilities = capabilities.encoderCapabilities
        capabilities.videoCapabilities
        capabilities.colorFormats
    }
}