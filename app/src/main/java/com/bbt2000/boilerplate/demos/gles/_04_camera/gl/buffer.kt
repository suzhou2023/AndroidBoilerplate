package com.bbt2000.boilerplate.demos.gles._04_camera.gl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 *  author : sz
 *  date : 2023/7/17
 *  description :
 */
object Buffer {
    var vertexData = floatArrayOf(
        -1.0f, 1.0f, 0.0f,  // top left
        -1.0f, -1.0f, 0.0f,  // bottom left
        1.0f, -1.0f, 0.0f,  // bottom right
        1.0f, 1.0f, 0.0f  // top right
    )

    var texData = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f
    )

    var indexData = shortArrayOf(3, 2, 0, 0, 1, 2)

    fun createBuffer(data: FloatArray): FloatBuffer {
        val buffer = ByteBuffer
            .allocateDirect(data.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        buffer.put(data, 0, data.size)
            .position(0)

        return buffer
    }

    fun createBuffer(data: ShortArray): ShortBuffer {
        val buffer = ByteBuffer
            .allocateDirect(data.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()

        buffer.put(data, 0, data.size)
            .position(0)

        return buffer
    }
}