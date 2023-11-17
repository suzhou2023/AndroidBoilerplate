#version 300 es
// 简单顶点着色器，顶点坐标、纹理坐标、顶点矩阵、纹理坐标翻转+矩阵
layout (location = 0)
in vec4 v_position;
layout (location = 1)
in vec2 tex_coord;

out vec2 tex_coord2;
uniform mat4 v_matrix;// 顶点坐标矩阵
uniform mat3 tex_matrix;// 纹理坐标矩阵

void main() {
    gl_Position = v_matrix * v_position;
    vec3 tex_vec = tex_matrix * vec3(tex_coord.x, 1.0 - tex_coord.y, 1);
    tex_coord2 = vec2(tex_vec.x, tex_vec.y);
}
