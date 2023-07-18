package com.bbt2000.boilerplate.demos.gles._04_camera


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
    fun loadShader(type: Int, shaderSource: String?): Int {
        val shader = glCreateShader(type)
        if (shader <= 0) return -1

        glShaderSource(shader, shaderSource)
        glCompileShader(shader)

        return shader
    }

    fun createProgram(verShader: Int, fragShader: Int): Int {
        val program = glCreateProgram()
        if (program <= 0) return -1

        glAttachShader(program, verShader)
        glAttachShader(program, fragShader)

        glLinkProgram(program)
        glUseProgram(program)

        return program
    }
}