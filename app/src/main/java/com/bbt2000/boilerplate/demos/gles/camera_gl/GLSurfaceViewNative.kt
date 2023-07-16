package com.bbt2000.boilerplate.demos.gles.camera_gl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.media.MediaPlayer.OnVideoSizeChangedListener
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import java.nio.ByteBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *  author : suzhou
 *  date : 2023/7/16
 *  description :
 */
class GLSurfaceViewNative : GLSurfaceView {
    private val mTestBmp: Bitmap? = null
    private var mRenderer: Renderer? = null
    private var mDemoPlayer: Player? = null

    /**图层native指针 */
    private var mLayer = Long.MIN_VALUE
    private var mRenderOES = Long.MIN_VALUE
    private var mRenderNoiseReduction = Long.MIN_VALUE
    private val mRenderConvolutionDemo = Long.MIN_VALUE
    private var mRenderLut = Long.MIN_VALUE
    private var mRenderDeBackground = Long.MIN_VALUE

    //Android画面数据输入Surface
    private var mDataInputSurface: Surface? = null

    //Android画面数据输入纹理
    private var mDataInputTexturesPointer: IntArray? = null
    private var mInputDataSurfaceTexture: SurfaceTexture? = null

    //    private var mDemoPlayer: Player? = null
    private var mdx = 0f
    private var mdy = 0f
    private var mPrevX = 0f
    private var mPrevY = 0f

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
        Log.i("cjztest", "NativeGLSurfaceView222")
    }

    private fun init() {
        setEGLContextClientVersion(3) //使用OpenGL ES 3.0需设置该参数为3
        mRenderer = Renderer() //创建Renderer类的对象
        setRenderer(mRenderer) //设置渲染器
        this.renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun getSurface(): Surface? {
        Log.i("cjztest", "GLRenderer.getSurface：" + mDataInputSurface.toString())
        return mDataInputSurface
    }

    /**亮度调整 */
    fun setRenderBrightness(brightness: Float) {
        if (mRenderOES != Long.MIN_VALUE) {
            JniBridge.setBrightness(mRenderOES, brightness)
        }
    }

    /**对比度调整 */
    fun setRenderContrast(contrast: Float) {
        if (mRenderOES != Long.MIN_VALUE) {
            JniBridge.setContrast(mRenderOES, contrast)
        }
    }

    /**白平衡调整 */
    fun setRenderWhiteBalance(rWeight: Float, gWeight: Float, bWeight: Float) {
        if (mRenderOES != Long.MIN_VALUE) {
            JniBridge.setWhiteBalance(mRenderOES, rWeight, gWeight, bWeight)
        }
    }

    /**降噪渲染器开关 */
    fun setRenderNoiseReductionOnOff(sw: Boolean) {
        if (mLayer != Long.MIN_VALUE) {
            if (mRenderNoiseReduction != Long.MIN_VALUE) {
                if (sw) {
                    JniBridge.addRenderToLayer(mLayer, mRenderNoiseReduction)
                } else {
                    JniBridge.removeRenderForLayer(mLayer, mRenderNoiseReduction)
                }
            }
        }
    }

    /**滤镜开关 */
    fun setRenderLutOnOff(sw: Boolean) {
        if (mLayer != Long.MIN_VALUE && mRenderLut != Long.MIN_VALUE) {
            if (sw) {
                JniBridge.addRenderToLayer(mLayer, mRenderLut)
            } else {
                JniBridge.removeRenderForLayer(mLayer, mRenderLut)
            }
        }
    }

    /**背景去除程序开关 */
    fun setRenderDeBackgroundOnOff(sw: Boolean) {
        if (mLayer != Long.MIN_VALUE && mRenderDeBackground != Long.MIN_VALUE) {
            if (sw) {
                JniBridge.addRenderToLayer(mLayer, mRenderDeBackground)
            } else {
                JniBridge.removeRenderForLayer(mLayer, mRenderDeBackground)
            }
        }
    }

    /**长宽缩放 */
    fun setScale(sx: Float, sy: Float) {
        if (mLayer != Long.MIN_VALUE) {
            JniBridge.layerScale(mLayer, sx, sy)
        }
    }

    /**移动 */
    fun setTrans(x: Float, y: Float) {
        if (mLayer != Long.MIN_VALUE) {
            JniBridge.layerTranslate(mLayer, x, y)
        }
    }

    /**旋转 */
    fun setRotate(angle: Float) {
        if (mLayer != Long.MIN_VALUE) {
            JniBridge.layerRotate(mLayer, angle)
        }
    }

    /**加载滤镜 */
    fun setLut(lutBMP: Bitmap) {
        if (mLayer != Long.MIN_VALUE && mRenderLut != Long.MIN_VALUE) {
            val b = ByteArray(lutBMP.byteCount)
            val bb = ByteBuffer.wrap(b)
            lutBMP.copyPixelsToBuffer(bb)
            JniBridge.renderLutTextureLoad(mRenderLut, b, lutBMP.width, lutBMP.height, lutBMP.width)
            Log.i("cjztest", "lut pixels size:" + lutBMP.byteCount)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPrevX = event.x
                mPrevY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                mdx += (event.x - mPrevX) / width
                mdy -= (event.y - mPrevY) / height
                setTrans(mdx, mdy)
                mPrevX = event.x
                mPrevY = event.y
            }
        }
        return true
    }

    private inner class Renderer : GLSurfaceView.Renderer {
        private var mWidth = 0
        private var mHeight = 0
        private var mVideoWidth = 0
        private var mVideoHeight = 0
        private var mIsFirstFrame = true
        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            Log.i("cjztest", String.format("NativeGlSurfaceView.onSurfaceCreated"))
            mWidth = 0
            mHeight = 0
            mVideoWidth = 0
            mVideoHeight = 0
            mIsFirstFrame = true
            //创建一个OES纹理和相关配套对象
            if (mDataInputSurface == null) {
                //创建OES纹理
                mDataInputTexturesPointer = IntArray(1)
                GLES30.glGenTextures(1, mDataInputTexturesPointer, 0)
                GLES30.glBindTexture(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    mDataInputTexturesPointer!![0]
                )
                //设置放大缩小。设置边缘测量
                GLES30.glTexParameterf(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat()
                )
                GLES30.glTexParameterf(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat()
                )
                GLES30.glTexParameteri(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE
                )
                GLES30.glTexParameteri(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE
                )
                mInputDataSurfaceTexture = SurfaceTexture(mDataInputTexturesPointer!![0])
                mDataInputSurface = Surface(mInputDataSurfaceTexture)
            }
            //创建一个demo播放器
            if (mDemoPlayer == null) {
                mDemoPlayer = Player(context, getSurface()!!,
                    OnVideoSizeChangedListener { mp, width, height ->
                        /**设置OES图层内容得大小 */
                        /**设置OES图层内容得大小 */
                        /**设置OES图层内容得大小 */

                        /**设置OES图层内容得大小 */
                        if ((width != mVideoWidth || height != mVideoHeight) && width > 0 && height > 0) {
                            Log.i(
                                "cjztest",
                                String.format("onSurfaceChanged: w:%d, h:%d", width, height)
                            )
                            mVideoWidth = width
                            mVideoHeight = height
                        }
                    })
            }
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            if ((width != mWidth || height != mHeight) && width > 0 && height > 0) {
                mWidth = width
                mHeight = height
                JniBridge.nativeGLInit(width, height)
                mIsFirstFrame = true
            }
        }

        override fun onDrawFrame(gl: GL10) {
            if (mIsFirstFrame) {  //不能异步进行gl操作，所以只能移到第一帧（或glrender的各种回调中，但这里需要等待onVideoSizeChanged准备好）进行图层创建
                if (mVideoWidth > 0 && mVideoHeight > 0) {
                    //清除上次用过的图层
                    if (mLayer != Long.MIN_VALUE) {
                        JniBridge.removeLayer(mLayer)
                    }
                    //创建一个图层（由于这个使用场景种没有数组数据，只有OES纹理，所以dataPointer为0）
                    mLayer = JniBridge.addFullContainerLayer(
                        mDataInputTexturesPointer!![0],
                        intArrayOf(mVideoWidth, mVideoHeight),
                        0,
                        intArrayOf(0, 0),
                        GLES30.GL_RGBA
                    ) //依次传入纹理、纹理的宽高、数据地址（如果有）、数据的宽高
                    //添加一个oes渲染器
                    mRenderOES =
                        JniBridge.makeRender(JniBridge.RENDER_PROGRAM_KIND.RENDER_OES_TEXTURE.ordinal) //添加oes纹理

//                    mRenderConvolutionDemo = JniBridge.addRenderForLayer(mLayer, JniBridge.RENDER_PROGRAM_KIND.RENDER_CONVOLUTION.ordinal()); //添加卷积图像处理demo
                    mRenderNoiseReduction =
                        JniBridge.makeRender(JniBridge.RENDER_PROGRAM_KIND.NOISE_REDUCTION.ordinal) //添加降噪渲染器
                    mRenderLut =
                        JniBridge.makeRender(JniBridge.RENDER_PROGRAM_KIND.RENDER_LUT.ordinal) //添加Lut渲染器
                    mRenderDeBackground =
                        JniBridge.makeRender(JniBridge.RENDER_PROGRAM_KIND.DE_BACKGROUND.ordinal) //创建背景去除渲染程序
                    JniBridge.addRenderToLayer(mLayer, mRenderOES)
                    JniBridge.addRenderToLayer(mLayer, mRenderNoiseReduction)
                    mIsFirstFrame = false
                }
            }
            mInputDataSurfaceTexture!!.updateTexImage()
            JniBridge.renderLayer(0, mWidth, mHeight)
        }
    }
}