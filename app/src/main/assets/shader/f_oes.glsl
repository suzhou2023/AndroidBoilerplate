#version 300 es
// OES片段着色器
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
in vec2 texCoord2;
out vec4 color;

uniform samplerExternalOES oesTexture;

void main()
{
    color = texture(oesTexture, texCoord2);
}
