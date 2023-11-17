#version 300 es
precision mediump float;
// 输入的纹理坐标
in vec2 tex_coord2;
out vec4 color;

// 用来采样的三个纹理
uniform sampler2D y_texture;
uniform sampler2D u_texture;
uniform sampler2D v_texture;

void main() {
    // 采样到的yuv向量数据
    vec3 yuv;
    // yuv转化得到的rgb向量数据
    vec3 rgb;
    // 分别取yuv各个分量的采样纹理
    yuv.x = texture(y_texture, tex_coord2).r;
    yuv.y = texture(u_texture, tex_coord2).r - 0.5;
    yuv.z = texture(v_texture, tex_coord2).r - 0.5;
    // yuv转rgb
    rgb = mat3(
    1.0, 1.0, 1.0,
    0.0, -0.183, 1.816,
    1.540, -0.459, 0.0
    ) * yuv;

    color = vec4(rgb, 1.0);
}