#version 300 es
layout (location = 0) in vec4 aPosition;
//新增的接收纹理坐标的变量
layout (location = 1) in vec2 aTexCoord;
//纹理坐标输出给片段着色器使用
out vec2 TexCoord;

void main() {
    //直接把传入的坐标值作为传入渲染管线。gl_Position是OpenGL内置的
    gl_Position = aPosition;
    //纹理坐标传给片段着色器
    TexCoord = aTexCoord;
}


#version 300 es
precision mediump float;
//新增的接收纹理坐标的变量
in vec2 TexCoord;
out vec4 FragColor;
//传入的纹理
uniform sampler2D ourTexture;

void main() {
    //texture方法执行具体的采样
    FragColor = texture(ourTexture, TexCoord);
}
