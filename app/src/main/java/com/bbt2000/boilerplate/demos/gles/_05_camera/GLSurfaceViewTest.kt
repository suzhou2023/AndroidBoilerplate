package com.bbt2000.boilerplate.demos.gles._05_camera

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLES20.GL_TRIANGLES
import android.opengl.GLES20.GL_UNSIGNED_SHORT
import android.opengl.GLES20.glActiveTexture
import android.opengl.GLES20.glDrawElements
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUniform1i
import android.opengl.GLES20.glVertexAttribPointer
import android.opengl.Matrix
import android.util.AttributeSet
import android.util.Log
import com.bbt2000.boilerplate.demos.gles._05_camera.gl.Buffer.createBuffer
import com.bbt2000.boilerplate.demos.gles._05_camera.gl.Buffer.indexData
import com.bbt2000.boilerplate.demos.gles._05_camera.gl.Buffer.texData
import com.bbt2000.boilerplate.demos.gles._05_camera.gl.Buffer.vertexData
import com.bbt2000.boilerplate.demos.gles._05_camera.gl.Shader.F_SHADER
import com.bbt2000.boilerplate.demos.gles._05_camera.gl.Shader.V_SHADER
import com.bbt2000.boilerplate.demos.gles._05_camera.gl.Tex.createOESTexture
import com.bbt2000.boilerplate.demos.gles._05_camera.gl.Util.createProgram
import com.bbt2000.boilerplate.demos.gles.widget.AutoFitGLSurfaceView
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 *  author : sz
 *  date : 2023/7/13 18:22
 *  description :
 */
class GLSurfaceViewTest(context: Context, attributeSet: AttributeSet? = null) :
    AutoFitGLSurfaceView(context, attributeSet) {

    init {
        setEGLContextClientVersion(3)
        setRenderer(GLRenderer())
    }

    private var mSurfaceTexture: SurfaceTexture? = null
    private var mOESTextureId: Int = -1

    private var mProgram: Int = -1
    private var vPositionLoc: Int = -1
    private var texCoordLoc: Int = -1
    private var textureLoc: Int = -1
    private var matrixLoc: Int = -1

    private lateinit var mVertexBuffer: FloatBuffer
    private lateinit var mTexBuffer: FloatBuffer
    private lateinit var mIndexBuffer: ShortBuffer

    private val rotateMatrix = FloatArray(16)

    fun isAvailable() = mSurfaceTexture != null
    fun getTexture() = mSurfaceTexture


    inner class GLRenderer : Renderer {
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            Log.d(TAG, "Surface created.")
            mOESTextureId = createOESTexture()
            mSurfaceTexture = SurfaceTexture(mOESTextureId)
            mSurfaceTexture?.setOnFrameAvailableListener {
                queueEvent {
                    mSurfaceTexture?.updateTexImage()
                    requestRender()
                    mSurfaceTexture?.getTransformMatrix(rotateMatrix)
                    Log.d(TAG, "rotateMatrix = ${rotateMatrix.contentToString()}")
                }
            }

            mProgram = createProgram(V_SHADER, F_SHADER)
            vPositionLoc = glGetAttribLocation(mProgram, "a_Position")
            texCoordLoc = glGetAttribLocation(mProgram, "a_TexCoord")
            textureLoc = glGetUniformLocation(mProgram, "u_Texture")
            matrixLoc = glGetUniformLocation(mProgram, "matrix")
            mVertexBuffer = createBuffer(vertexData)
            mTexBuffer = createBuffer(texData)
            mIndexBuffer = createBuffer(indexData)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            Log.d(TAG, "Surface changed.")
            Matrix.setIdentityM(rotateMatrix, 0);
//            Matrix.rotateM(rotateMatrix, 0, 270f, 0f, 0f, 1f)
        }

        override fun onDrawFrame(gl: GL10?) {
            // 设置顶点数据
            mVertexBuffer.position(0)
            glEnableVertexAttribArray(vPositionLoc)
            glVertexAttribPointer(vPositionLoc, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer)
            // 设置纹理坐标数据
            mTexBuffer.position(0)
            glEnableVertexAttribArray(texCoordLoc)
            glVertexAttribPointer(texCoordLoc, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer)
            // 设置纹理
            glActiveTexture(GLES20.GL_TEXTURE0)
            glUniform1i(textureLoc, 0)
            // 矩阵
            GLES20.glUniformMatrix4fv(matrixLoc, 1, false, rotateMatrix, 0)
            glDrawElements(GL_TRIANGLES, indexData.size, GL_UNSIGNED_SHORT, mIndexBuffer)
        }
    }

    companion object {
        private const val TAG = "GLSurfaceViewTest"
    }
}





