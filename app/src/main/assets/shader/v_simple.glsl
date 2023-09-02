#version 300 es
// 简单顶点着色器，顶点坐标、纹理坐标、纹理坐标翻转
layout (location = 0)
in vec4 v_position;
layout (location = 1)
in vec2 texCoord;

out vec2 texCoord2;

void main() {
    gl_Position = v_position;
    texCoord2 = texCoord;
}
