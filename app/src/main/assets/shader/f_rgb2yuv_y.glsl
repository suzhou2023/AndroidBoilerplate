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
    vec4 rgb0, rgb1, rgb2, rgb3;
    // 4次采样，纹理坐标做一定的偏移
    rgb0 = texture(texture2d, vec2(texCoord2.x - offset * 1.5, texCoord2.y));
    rgb1 = texture(texture2d, vec2(texCoord2.x - offset * 0.5, texCoord2.y));
    rgb2 = texture(texture2d, vec2(texCoord2.x + offset * 0.5, texCoord2.y));
    rgb3 = texture(texture2d, vec2(texCoord2.x + offset * 1.5, texCoord2.y));

    vec3 yuv0, yuv1, yuv2, yuv3;
    // 套公式rgb转yuv
    // todo: 这里的矩阵顺序我真的有点晕，别人文章上说是列主序，可我用列主序需要改成右乘向量结果才正确
    // BT709 full range
    //    yuv0 = rgb0.rgb * mat3(
    //        0.183, -0.101, 0.439,
    //        0.614, -0.339, -0.339,
    //        0.062, 0.439, -0.040
    //    ) + (16.0 / 256.0, 0.5, 0.5);

    // BT709 limited range
    //    yuv0 = rgb0.rgb * mat3(
    //        0.213, -0.117, 0.511,
    //        0.715, -0.394, -0.464,
    //        0.072, 0.511, -0.047
    //    ) + (0, 0.5, 0.5);

    // 4个点，每个点只用管亮度信息Y
    yuv0.r = 0.213 * rgb0.r + 0.715 * rgb0.g + 0.072 * rgb0.b;
    yuv1.r = 0.213 * rgb1.r + 0.715 * rgb1.g + 0.072 * rgb1.b;
    yuv2.r = 0.213 * rgb2.r + 0.715 * rgb2.g + 0.072 * rgb2.b;
    yuv3.r = 0.213 * rgb3.r + 0.715 * rgb3.g + 0.072 * rgb3.b;

    color = vec4(yuv0.r, yuv1.r, yuv2.r, yuv3.r);
}