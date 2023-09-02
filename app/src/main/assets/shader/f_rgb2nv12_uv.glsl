#version 300 es
precision mediump float;
// 输入的纹理坐标
in vec2 texCoord2;

// 输出坐标点颜色
out vec4 color;

// 用来采样的2d纹理
uniform sampler2D texture2d;

// 偏移采样的偏移量
uniform float offset;

void main() {
    vec4 rgb0, rgb1;
    // 2次采样，纹理坐标做一定的偏移
    rgb0 = texture(texture2d, vec2(texCoord2.x - offset, texCoord2.y));
    rgb1 = texture(texture2d, vec2(texCoord2.x + offset, texCoord2.y));

    // 两组UV
    vec3 yuv0, yuv1;
    // BT709 full range
    // 2次采样，两组UV
    yuv0.g = -0.101 * rgb0.r - 0.339 * rgb0.g + 0.439 * rgb0.b + 0.5;
    yuv0.b = 0.439 * rgb0.r - 0.339 * rgb0.g - 0.040 * rgb0.b + 0.5;
    yuv1.g = -0.101 * rgb1.r - 0.339 * rgb1.g + 0.439 * rgb1.b + 0.5;
    yuv1.b = 0.439 * rgb1.r - 0.339 * rgb1.g - 0.040 * rgb1.b + 0.5;

    // UVUV排列
    color = vec4(yuv0.g, yuv0.b, yuv1.g, yuv1.b);
}