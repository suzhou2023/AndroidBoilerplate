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

    external fun openStream(url: String)
}