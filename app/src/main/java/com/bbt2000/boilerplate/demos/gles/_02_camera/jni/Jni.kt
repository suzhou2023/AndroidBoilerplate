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

    external fun nativeCreateGLContext(surface: Any, otherGLContext: Long = 0): Long
    external fun nativeEglMakeCurrent(glContext: Long = 0): Boolean
    external fun nativeConfigGL(glContext: Long = 0)
    external fun nativeCreateOESTexture(glContext: Long): Int
    external fun nativeSetMatrix(glContext: Long, matrix: FloatArray)
    external fun nativeDrawFrame(glContext: Long)
    external fun nativeDestroyGLContext(glContext: Long = 0)
}