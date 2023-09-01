package com.bbt2000.boilerplate.demos.gles.jni

import android.content.res.AssetManager

/**
 *  author : sz
 *  date : 2023/7/31
 *  description :
 */
object Jni {

    /**
     * 创建GL context
     *
     * @param otherGLContext
     * @param assetManager
     * @return
     */
    external fun nativeCreateGLContext(otherGLContext: Long = 0, assetManager: AssetManager): Long

    /**
     * 创建EGL surface
     *
     * @param glContext
     * @param surface
     * @param index
     * @return
     */
    external fun nativeEGLCreateSurface(glContext: Long, surface: Any, index: Int = 0): Boolean

    /**
     * 创建并链接着色器程序
     *
     * @param glContext
     */
    external fun nativeCreateProgram(glContext: Long, vName: String, fName: String)

    /**
     * 加载顶点属性数组
     *
     * @param glContext
     */
    external fun nativeLoadVertices(glContext: Long)

    /**
     * 通知native窗口变化
     *
     * @param glContext
     * @param format
     * @param width
     * @param height
     */
    external fun nativeSurfaceChanged(glContext: Long, format: Int, width: Int, height: Int)

    /**
     * 创建OES纹理
     *
     * @param glContext
     * @return
     */
    external fun nativeCreateOESTexture(glContext: Long): Int

    /**
     * 创建FBO
     *
     * @param glContext
     * @param width
     * @param height
     */
    external fun nativeCreateFbo(glContext: Long, width: Int, height: Int)

    /**
     * 设置变换矩阵
     *
     * @param glContext
     * @param matrix
     */
    external fun nativeSetMatrix(glContext: Long, matrix: FloatArray)

    /**
     * 绘制
     *
     * @param glContext
     */
    external fun nativeDrawFrame(glContext: Long)

    /**
     * 销毁glContext
     *
     * @param glContext
     */
    external fun nativeDestroyGLContext(glContext: Long)

}