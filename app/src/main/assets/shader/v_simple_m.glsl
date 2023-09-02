#version 300 es
// 简单顶点着色器，顶点坐标、纹理坐标、顶点变换矩阵、纹理坐标翻转
layout (location = 0)
in vec4 v_position;
layout (location = 1)
in vec2 texCoord;

out vec2 texCoord2;
uniform mat4 matrix;

void main() {
    gl_Position = matrix * v_position;
    texCoord2 = texCoord;
}
