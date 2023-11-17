#version 300 es
// OES片段着色器
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
in vec2 tex_coord2;
out vec4 color;

uniform samplerExternalOES oes_texture;

void main()
{
    color = texture(oes_texture, tex_coord2);
}
