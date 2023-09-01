#version 300 es
precision mediump float;
// 输入的纹理坐标
in vec2 texCoord2;

// 采样器
uniform sampler2D texture2d;

// 一次偏移采样的偏移量
uniform float offset;

// 输出坐标点颜色
out vec4 color;

void main() {
    vec4 rgb0, rgb1;
    // 正常采样得到rgb0向量数据
    rgb0 = texture(texture2d, texCoord2);
    // 偏移采样得到的rgb1向量数据
    rgb1 = texture(texture2d, texCoord2 + (offset, 0.0));

    vec3 yuv0, yuv1;

    // 套公式rgb转yuv(采用BT709 limited range)
    yuv0 = mat3(
    0.213, -0.117, 0.511,
    0.715, -0.394, -0.464,
    0.072, 0.511, -0.047
    ) * rgb0.rgb + (0.0, 0.5, 0.5);

    yuv1 = mat3(
    0.213, -0.117, 0.511,
    0.715, -0.394, -0.464,
    0.072, 0.511, -0.047
    ) * rgb1.rgb + (0.0, 0.5, 0.5);

    // 得到(y0,u0,v0,y1)
    // 从opengl的角度，这是一个rgba的像素，但其实不是
    // 我们只是用一个2d纹理采了2次样，然后利用2次采样的数据计算了一通
    // 最后返回了一个4维向量给她，她可能认为是一个颜色吧
    // 然后苦苦等待着我们把她含辛茹苦采样和计算的得到的“颜色”绘制到屏幕上
    // 她期待着看到屏幕上的绚丽图案时，能够会心一笑...
    // 结果你却没有这样做，你残忍地把她送你的绚丽颜色都用glReadPixel偷走了
    color = vec4(yuv0.x, yuv0.y, yuv0.z, yuv1.x);
}