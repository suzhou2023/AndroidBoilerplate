package com.bbt2000.boilerplate.demos.gles._02_camera.jni

/**
 *  author : sz
 *  date : 2023/7/31
 *  description :
 */
object Jni {
    init {
        System.loadLibrary("gl_render")
    }

    external fun nativeCreateGLContext(otherGLContext: Long = 0): Long
    external fun nativeEGLCreateSurface(glContext: Long, surface: Any, index: Int = 0): Boolean
    external fun nativeConfigGL(glContext: Long = 0)
    external fun nativeCreateOESTexture(glContext: Long): Int
    external fun nativeCreateFbo(glContext: Long, width: Int, height: Int)
    external fun nativeSetMatrix(glContext: Long, matrix: FloatArray)
    external fun nativeDrawFrame(glContext: Long)
    external fun nativeDestroyGLContext(glContext: Long = 0)
}