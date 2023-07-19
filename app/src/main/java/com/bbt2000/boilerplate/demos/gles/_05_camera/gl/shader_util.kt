package com.bbt2000.boilerplate.demos.gles._05_camera.gl


import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glAttachShader
import android.opengl.GLES20.glCompileShader
import android.opengl.GLES20.glCreateProgram
import android.opengl.GLES20.glCreateShader
import android.opengl.GLES20.glLinkProgram
import android.opengl.GLES20.glShaderSource
import android.opengl.GLES20.glUseProgram


/**
 *  author : sz
 *  date : 2023/7/17
 *  description :
 */
object Util {
    private fun loadShader(type: Int, shaderSource: String?): Int {
        val shader = glCreateShader(type)
        if (shader <= 0) return -1

        glShaderSource(shader, shaderSource)
        glCompileShader(shader)

        return shader
    }

    fun createProgram(vShaderStr: String, fShaderStr: String): Int {
        val vShader = loadShader(GL_VERTEX_SHADER, vShaderStr)
        if (vShader <= 0) return -1
        val fShader = loadShader(GL_FRAGMENT_SHADER, fShaderStr)
        if (fShader <= 0) return -1

        val program = glCreateProgram()
        if (program <= 0) return -1

        glAttachShader(program, vShader)
        glAttachShader(program, fShader)

        glLinkProgram(program)
        glUseProgram(program)

        return program
    }
}