/**
 *  author : sz
 *  date : 2023/7/18
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_SHADER_H
#define ANDROIDBOILERPLATE_SHADER_H


static const char V_SHADER[] =
        "#version 300 es\n"
        "layout (location = 0) \n"
        "in vec4 v_position;\n"
        "//新增的接收纹理坐标的变量\n"
        "layout (location = 1) \n"
        "in vec2 texCoord;\n"
        "//纹理坐标输出给片段着色器使用\n"
        "out vec2 texCoord2;\n"
        "\n"
        "void main() {\n"
        "    gl_Position = v_position;\n"
        "    //纹理坐标传给片段着色器\n"
        "    texCoord2 = texCoord;\n"
        "}";


static const char F_SHADER[] =
        "#version 300 es\n"
        "precision mediump float;\n"
        "//纹理坐标\n"
        "in vec2 texCoord2;\n"
        "//输入的yuv三个纹理\n"
        "uniform sampler2D yTexture;//采样器\n"
        "uniform sampler2D uTexture;//采样器\n"
        "uniform sampler2D vTexture;//采样器\n"
        "\n"
        "out vec4 fragColor;\n"
        "void main() {\n"
        "    // 采样到的yuv向量数据\n"
        "    vec3 yuv;\n"
        "    // yuv转化得到的rgb向量数据\n"
        "    vec3 rgb;\n"
        "    // 分别取yuv各个分量的采样纹理\n"
        "    yuv.x = texture(yTexture, texCoord2).r;\n"
        "    yuv.y = texture(uTexture, texCoord2).g - 0.5;\n"
        "    yuv.z = texture(vTexture, texCoord2).b - 0.5;\n"
        "    //yuv转化为rgb\n"
        "    rgb = mat3(\n"
        "        1.0, 1.0, 1.0,\n"
        "        0.0, -0.183, 1.816,\n"
        "        1.540, -0.459, 0.0\n"
        "    ) * yuv;\n"
        "    //gl_FragColor是OpenGL内置的\n"
        "    fragColor = vec4(rgb, 1.0);\n"
        "}";


#endif //ANDROIDBOILERPLATE_SHADER_H
