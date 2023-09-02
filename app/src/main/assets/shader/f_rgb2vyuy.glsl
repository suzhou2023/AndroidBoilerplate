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
    // BT709 full range
    //    yuv0 = rgb0.rgb * mat3(
    //        0.183, -0.101, 0.439,
    //        0.614, -0.339, -0.339,
    //        0.062, 0.439, -0.040
    //    ) + (16.0 / 256.0, 0.5, 0.5);

    vec4 rgb0, rgb1;
    vec3 yuv0, yuv1;

    // 2次采样，得到2个点的Y信息
    rgb0 = texture(texture2d, vec2(texCoord2.x - offset * 0.5, texCoord2.y));
    rgb1 = texture(texture2d, vec2(texCoord2.x + offset * 0.5, texCoord2.y));

    yuv0.r = 0.183 * rgb0.r + 0.614 * rgb0.g + 0.062 * rgb0.b + 16.0 / 256.0;
    yuv1.r = 0.183 * rgb1.r + 0.614 * rgb1.g + 0.062 * rgb1.b + 16.0 / 256.0;


    vec4 rgb2;
    vec3 yuv2;

    // 1次采样，1组UV
    rgb2 = texture(texture2d, vec2(texCoord2.x, texCoord2.y));

    yuv2.g = -0.101 * rgb2.r - 0.339 * rgb2.g + 0.439 * rgb2.b + 0.5;
    yuv2.b = 0.439 * rgb2.r - 0.339 * rgb2.g - 0.040 * rgb2.b + 0.5;

    // VYUY排列
    color = vec4(yuv2.b, yuv0.r, yuv2.g, yuv1.r);
}