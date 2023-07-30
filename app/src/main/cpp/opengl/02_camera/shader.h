/**
 *  author : sz
 *  date : 2023/7/18
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_SHADER_TEX_H
#define ANDROIDBOILERPLATE_SHADER_H


static const char V_SHADER[] =
        "#version 300 es\n"
        "layout (location = 0) \n"
        "in vec4 v_position;\n"
        "layout (location = 1) \n"
        "in vec2 texCoord;\n"
        "out vec2 texCoord2;\n"
        "uniform mat4 matrix;\n"
        "void main() {\n"
        "    gl_Position = matrix * v_position;\n"
        "    //bitmap/oes纹理需要做翻转？？？\n"
        "    texCoord2 = vec2(texCoord.x, 1.0 - texCoord.y);\n"
        "}";


static const char F_SHADER[] =
        "#version 300 es\n"
        "#extension GL_OES_EGL_image_external_essl3 : require\n"
        "precision mediump float;\n"
        "in vec2 texCoord2;\n"
        "uniform samplerExternalOES oesTexture;\n"
        "out vec4 fColor;\n"
        "void main()\n"
        "{\n"
        "    fColor = texture(oesTexture, texCoord2);\n"
        "}";


#endif //ANDROIDBOILERPLATE_SHADER_TEX_H
