#version 300 es
precision mediump float;
// 输入的纹理坐标
in vec2 texCoord2;
// 输入的yuv三个纹理
uniform sampler2D yTexture;//采样器
uniform sampler2D uTexture;//采样器
uniform sampler2D vTexture;//采样器

out vec4 color;

void main() {
    // 采样到的yuv向量数据
    vec3 yuv;
    // yuv转化得到的rgb向量数据
    vec3 rgb;
    // 分别取yuv各个分量的采样纹理
    yuv.x = texture(yTexture, texCoord2).r;
    yuv.y = texture(uTexture, texCoord2).r - 0.5;
    yuv.z = texture(vTexture, texCoord2).r - 0.5;
    // yuv转rgb
    rgb = mat3(
    1.0, 1.0, 1.0,
    0.0, -0.183, 1.816,
    1.540, -0.459, 0.0
    ) * yuv;

    color = vec4(rgb, 1.0);
}