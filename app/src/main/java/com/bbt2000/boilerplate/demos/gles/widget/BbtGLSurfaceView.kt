package com.bbt2000.boilerplate.demos.gles.widget

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLES20.GL_FLOAT
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_TRIANGLES
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glActiveTexture
import android.opengl.GLES20.glAttachShader
import android.opengl.GLES20.glBindTexture
import android.opengl.GLES20.glCompileShader
import android.opengl.GLES20.glCreateProgram
import android.opengl.GLES20.glCreateShader
import android.opengl.GLES20.glDrawArrays
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetError
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glLinkProgram
import android.opengl.GLES20.glShaderSource
import android.opengl.GLES20.glUniform1i
import android.opengl.GLES20.glUniformMatrix4fv
import android.opengl.GLES20.glUseProgram
import android.opengl.GLES20.glVertexAttribPointer
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 *  author : sz
 *  date : 2023/7/13 18:22
 *  description :
 */

class BbtGLSurfaceView : AutoFitGLSurfaceView, GLSurfaceView.Renderer {
    private var mSurfaceTexture: SurfaceTexture? = null

    private var mOESTextureId: Int = -1
    private var mShaderProgram: Int = -1
    private lateinit var mDataBuffer: FloatBuffer

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        setEGLContextClientVersion(2)
        setRenderer(this)

//        val vertexShader = loadShader(GL_VERTEX_SHADER, VERTEX_SHADER);
//        val fragmentShader = loadShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
//        mShaderProgram = linkProgram(vertexShader, fragmentShader);
//
//        mDataBuffer = createBuffer(vertexData)
    }

    private val VERTEX_SHADER = "" +
            //顶点坐标
            "attribute vec4 aPosition;\n" +
            //纹理矩阵
            "uniform mat4 uTextureMatrix;\n" +
            //自己定义的纹理坐标
            "attribute vec4 aTextureCoordinate;\n" +
            //传给片段着色器的纹理坐标
            "varying vec2 vTextureCoord;\n" +
            "void main()\n" +
            "{\n" +
            //根据自己定义的纹理坐标和纹理矩阵求取传给片段着色器的纹理坐标
            "  vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;\n" +
            "  gl_Position = aPosition;\n" +
            "}\n";

    private val FRAGMENT_SHADER = "" +
            //使用外部纹理必须支持此扩展
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            //外部纹理采样器
            "uniform samplerExternalOES uTextureSampler;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() \n" +
            "{\n" +
            //获取此纹理（预览图像）对应坐标的颜色值
            "  vec4 vCameraColor = texture2D(uTextureSampler, vTextureCoord);\n" +
            //求此颜色的灰度值
            "  float fGrayColor = (0.3*vCameraColor.r + 0.59*vCameraColor.g + 0.11*vCameraColor.b);\n" +
            //将此灰度值作为输出颜色的RGB值，这样就会变成黑白滤镜
            "  gl_FragColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);\n" +
            "}\n";

    //每行前两个值为顶点坐标，后两个为纹理坐标
    private val vertexData = floatArrayOf(
        1f, 1f, 1f, 1f,
        -1f, 1f, 0f, 1f,
        -1f, -1f, 0f, 0f,
        1f, 1f, 1f, 1f,
        -1f, -1f, 0f, 0f,
        1f, -1f, 1f, 0f
    )

    private fun createBuffer(vertexData: FloatArray): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(vertexData, 0, vertexData.size).position(0)
        return buffer
    }

    //加载着色器，GL_VERTEX_SHADER代表生成顶点着色器，GL_FRAGMENT_SHADER代表生成片段着色器
    fun loadShader(type: Int, shaderSource: String?): Int {
        //创建Shader
        val shader = glCreateShader(type)
        if (shader == 0) {
            throw RuntimeException("Create Shader Failed!" + glGetError())
        }
        //加载Shader代码
        glShaderSource(shader, shaderSource)
        //编译Shader
        glCompileShader(shader)
        return shader
    }

    //将两个Shader链接至program中
    fun linkProgram(verShader: Int, fragShader: Int): Int {
        //创建program
        val program = glCreateProgram()
        if (program == 0) {
            throw RuntimeException("Create Program Failed!" + glGetError())
        }
        //附着顶点和片段着色器
        glAttachShader(program, verShader)
        glAttachShader(program, fragShader)
        //链接program
        glLinkProgram(program)
        //告诉OpenGL ES使用此program
        glUseProgram(program)
        return program
    }

    fun isAvailable() = mSurfaceTexture != null
    fun getTexture() = mSurfaceTexture

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        mOESTextureId = createOESTextureObject()
        initSurfaceTexture()

        val vertexShader = loadShader(GL_VERTEX_SHADER, VERTEX_SHADER);
        val fragmentShader = loadShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        mShaderProgram = linkProgram(vertexShader, fragmentShader);

        mDataBuffer = createBuffer(vertexData)
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
    }

    override fun onDrawFrame(p0: GL10?) {
        //获取Shader中定义的变量在program中的位置
        var aPositionLocation = glGetAttribLocation(mShaderProgram, "aPosition");
        var aTextureCoordLocation = glGetAttribLocation(mShaderProgram, "aTextureCoordinate");
        var uTextureMatrixLocation = glGetUniformLocation(mShaderProgram, "uTextureMatrix");
        var uTextureSamplerLocation = glGetUniformLocation(mShaderProgram, "uTextureSampler");

        //激活纹理单元0
        glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定外部纹理到纹理单元0
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        //将此纹理单元床位片段着色器的uTextureSampler外部纹理采样器
        glUniform1i(uTextureSamplerLocation, 0);

        //将纹理矩阵传给片段着色器
//        glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0);

        //将顶点和纹理坐标传给顶点着色器
        if (mDataBuffer != null) {
            //顶点坐标从位置0开始读取
            mDataBuffer.position(0);
            //使能顶点属性
            glEnableVertexAttribArray(aPositionLocation);
            //顶点坐标每次读取两个顶点值，之后间隔16（每行4个值 * 4个字节）的字节继续读取两个顶点值
            glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 16, mDataBuffer);

            //纹理坐标从位置2开始读取
            mDataBuffer.position(2);
            glEnableVertexAttribArray(aTextureCoordLocation);
            //纹理坐标每次读取两个顶点值，之后间隔16（每行4个值 * 4个字节）的字节继续读取两个顶点值
            glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 16, mDataBuffer);
        }

        //绘制两个三角形（6个顶点）
//        glDrawArrays(GL_TRIANGLES, 0, 6);
        //更新纹理图像
        mSurfaceTexture?.updateTexImage();
        //获取外部纹理的矩阵，用来确定纹理的采样位置，没有此矩阵可能导致图像翻转等问题
//            mSurfaceTexture?.getTransformMatrix(transformMatrix);
    }

    private fun initSurfaceTexture(): Boolean {
        //根据OES纹理ID实例化SurfaceTexture
        mSurfaceTexture = SurfaceTexture(mOESTextureId)
        //当SurfaceTexture接收到一帧数据时，请求OpenGL ES进行渲染
        mSurfaceTexture?.setOnFrameAvailableListener {
            requestRender()
        }
        return true
    }

    companion object {
        private const val TAG = "BbtGLSurfaceView2"
    }
}

fun createOESTextureObject(): Int {
    val tex = IntArray(1)
    GLES20.glGenTextures(1, tex, 0)
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0])
    GLES20.glTexParameterf(
        GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
        GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat()
    )
    GLES20.glTexParameterf(
        GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
        GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat()
    )
    GLES20.glTexParameterf(
        GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
        GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE.toFloat()
    )
    GLES20.glTexParameterf(
        GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
        GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE.toFloat()
    )
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
    return tex[0]
}



