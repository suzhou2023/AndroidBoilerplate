package com.bbt2000.boilerplate.demos.ffmpeg.jni

/**
 *  author : sz
 *  date : 2023/10/31
 *  description :
 */
object Jni {

    init {
        System.loadLibrary("ffmpeg-bbt")
    }

    external fun createFFContext(): Long
    external fun openRtspStream(ffContext: Long, url: String): Boolean
    external fun readFrames(ffContext: Long, glContext: Long)
    external fun readOneFrame(ffContext: Long, glContext: Long): Int
    external fun destroyFFContext(ffContext: Long)
}