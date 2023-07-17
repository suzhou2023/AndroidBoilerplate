package com.bbt2000.boilerplate.demos.gles._03_camera

/**
 *  author : sz
 *  date : 2023/7/17
 *  description :
 */

object Shader {
    const val VERTEX_SHADER = """
        attribute vec4 a_Position;
        attribute vec2 a_TexCoordinate;
        varying vec2 v_TexCoord;
        uniform mat4 matrix;
        void main()
        {
            v_TexCoord = a_TexCoordinate;
            gl_Position = a_Position;
        }
    """

    const val FRAGMENT_SHADER = """
        #extension GL_OES_EGL_image_external : require
        precision mediump float;
        uniform samplerExternalOES u_Texture;
        varying vec2 v_TexCoord;
        void main()
        {
            gl_FragColor = texture2D(u_Texture, v_TexCoord);
        }
    """
}
