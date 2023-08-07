package com.bbt2000.boilerplate.demos.gles._02_camera.encode

import android.media.MediaCodecList
import android.util.Log


/**
 *  author : suzhou
 *  date : 2023/8/4 21:27
 *  description :
 */
object CodecUtil {
    const val TAG = "CodecUtil"
    fun getCodecInfo(mimeType: String) {
        val codecInfo = MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos
            .find { it.isEncoder && it.supportedTypes.contains(mimeType) }
        codecInfo ?: return
        val capabilities = codecInfo.getCapabilitiesForType(mimeType)
        Log.d(TAG, "mimeType=${capabilities.mimeType}")
        Log.d(TAG, "colorFormats=${capabilities.colorFormats.contentToString()}")
        Log.d(TAG, "defaultFormat=${capabilities.defaultFormat}")
        for (format in capabilities.colorFormats) {
            Log.d(TAG, "colorFormats=${format.toString(10)}")
        }
        for (profileLevel in capabilities.profileLevels) {
            Log.d(TAG, "profile=${profileLevel.profile}")
            Log.d(TAG, "level=${profileLevel.level}")
        }
    }
}