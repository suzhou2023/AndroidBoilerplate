package com.bbt2000.boilerplate.demos.gles._04_camera.gl

/**
 *  author : sz
 *  date : 2023/7/17
 *  description :
 */

object Shader {
    const val V_SHADER = """
        attribute vec4 a_Position;
        attribute vec2 a_TexCoord;
        varying vec2 v_TexCoord;
        uniform mat4 matrix;
        void main()
        {
            gl_Position = matrix * a_Position;
            v_TexCoord = a_TexCoord;
            //v_TexCoord = vec2(a_TexCoord.x, 1.0 - a_TexCoord.y);
        }
    """

    const val F_SHADER = """
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
