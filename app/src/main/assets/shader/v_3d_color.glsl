#version 300 es
// 简单3d顶点着色器
layout (location = 0)
in vec4 v_position;
layout (location = 1)
in vec4 color;

out vec4 color2;// 输出的颜色

uniform mat4 m_model;// 模型矩阵
uniform mat4 m_view;// 观察矩阵
uniform mat4 m_proj;// 投影矩阵

void main() {
    gl_Position = m_proj * m_view * m_model * v_position;
    color2 = color;
}
