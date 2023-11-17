#version 300 es
// 简单顶点着色器，顶点坐标、纹理坐标、顶点变换矩阵、纹理坐标翻转
layout (location = 0)
in vec4 v_position;
layout (location = 1)
in vec2 tex_coord;

out vec2 tex_coord2;
uniform mat4 v_matrix;

void main() {
    gl_Position = v_matrix * v_position;
    tex_coord2 = tex_coord;
}
