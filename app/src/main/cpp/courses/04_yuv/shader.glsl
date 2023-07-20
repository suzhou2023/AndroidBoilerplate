#version 300 es
layout (location = 0)
in vec4 v_position;
//新增的接收纹理坐标的变量
layout (location = 1)
in vec2 texCoord;
//纹理坐标输出给片段着色器使用
out vec2 texCoord2;
//变换矩阵
uniform mat4 matrix;

void main() {
    gl_Position = matrix * v_position;
    //纹理坐标传给片段着色器
    texCoord2 = texCoord;
}


#version 300 es
precision mediump float;
//纹理坐标
in vec2 texCoord2;
//输入的yuv三个纹理
uniform sampler2D yTexture;//采样器
uniform sampler2D uTexture;//采样器
uniform sampler2D vTexture;//采样器

out vec4 fragColor;
void main() {
    // 采样到的yuv向量数据
    vec3 yuv;
    // yuv转化得到的rgb向量数据
    vec3 rgb;
    // 分别取yuv各个分量的采样纹理
    yuv.x = texture(yTexture, texCoord2).r;
    yuv.y = texture(uTexture, texCoord2).g - 0.5;
    yuv.z = texture(vTexture, texCoord2).b - 0.5;
    //yuv转化为rgb
    rgb = mat3(
    1.0, 1.0, 1.0,
    0.0, -0.183, 1.816,
    1.540, -0.459, 0.0
    ) * yuv;
    //gl_FragColor是OpenGL内置的
    fragColor = vec4(rgb, 1.0);
}

