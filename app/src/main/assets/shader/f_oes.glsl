#version 300 es
// OES片段着色器
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
in vec2 texCoord2;
uniform samplerExternalOES oesTexture;
out vec4 color;

void main()
{
    color = texture(oesTexture, texCoord2);
}
