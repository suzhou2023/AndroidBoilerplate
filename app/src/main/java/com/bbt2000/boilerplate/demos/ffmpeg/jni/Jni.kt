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

    // 创建FFContext
    external fun createFFContext(): Long

    // 打开rtsp流
    external fun openRtspStream(ffContext: Long, url: String): Boolean

    // 设置opengl渲染矩阵
    external fun glConfigMatrix(
        ffContext: Long, glContext: Long, programIndex: Int = 0,
        windowW: Int, windowH: Int, scaleType: Int = 1, rotate: Boolean = true
    )

    // 连续读取
    external fun readFrames(ffContext: Long, glContext: Long)

    // 读取并渲染一帧
    external fun readOneFrame(ffContext: Long, glContext: Long): Int

    // 销毁FFContext
    external fun destroyFFContext(ffContext: Long)
}





































