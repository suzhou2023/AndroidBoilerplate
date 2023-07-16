package com.bbt2000.boilerplate.demos.gles.camera_gl

import android.view.Surface

/**
 *  author : suzhou
 *  date : 2023/7/16
 *  description :
 */


object JniBridge {
    init {
        System.loadLibrary("opengl_decoder")
    }

    external fun nativeGLInit(viewPortWidth: Int, viewPortHeight: Int)

    //    public static native void drawRGBABitmap(Bitmap bmp, int bmpW, int bmpH);
    external fun drawToSurface(surface: Surface?, color: Int)
    external fun drawBuffer()
    external fun addFullContainerLayer(
        texturePointer: Int, textureWidthAndHeight: IntArray?, dataPointer: Long,
        dataWidthAndHeight: IntArray?,
        dataPixelFormat: Int
    ): Long

    external fun removeLayer(layerPointer: Long)

    /**创建渲染器
     * @param renderProgramKind 渲染器类型，参考RENDER_PROGRAM_KIND
     */
    external fun makeRender(renderProgramKind: Int): Long
    external fun addRenderToLayer(layerPointer: Long, renderPointer: Long)
    external fun removeRenderForLayer(layerPointer: Long, renderPointer: Long)
    external fun setRenderAlpha(renderPointer: Long, alpha: Float)

    /**渲染器亮度调整 */
    external fun setBrightness(renderPointer: Long, brightness: Float)

    /**渲染器对比度调整 */
    external fun setContrast(renderPointer: Long, contrast: Float)

    /**白平衡调整 */
    external fun setWhiteBalance(
        renderPointer: Long,
        redWeight: Float,
        greenWeight: Float,
        blueWeight: Float
    )

    external fun renderLayer(fboPointer: Int, fboWidth: Int, fboHeight: Int)
    external fun layerScale(layerPointer: Long, scaleX: Float, scaleY: Float)
    external fun layerTranslate(layerPointer: Long, dx: Float, dy: Float)
    external fun layerRotate(layerPointer: Long, angle: Float)

    /******************************************特定图层非通用功能设置区 */
    external fun renderLutTextureLoad(
        lutRenderPointer: Long,
        lutPixels: ByteArray?,
        w: Int,
        h: Int,
        unitLen: Int
    )

    /**渲染器类型枚举器 todo java要调用，则也要抄一份 */
    enum class RENDER_PROGRAM_KIND {
        RENDER_OES_TEXTURE,  //OES纹理渲染
        RENDER_YUV,  //YUV数据或纹理渲染
        RENDER_CONVOLUTION,  //添加卷积处理
        NOISE_REDUCTION,  //添加噪声处理
        RENDER_LUT,  //添加滤镜处理渲染器
        DE_BACKGROUND,  //去除背景
        BLUR_BACKGROUND
        //叠加一层视频内容到视频内容的背后，但让其模糊化
    }
}
