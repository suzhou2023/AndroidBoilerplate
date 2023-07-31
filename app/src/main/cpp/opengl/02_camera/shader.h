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
        "layout (location = 1) \n"
        "in vec2 texCoord;\n"
        "out vec2 fTexCoord;\n"
        "uniform mat4 matrix;\n"
        "void main() {\n"
        "    gl_Position = matrix * v_position;\n"
        "    fTexCoord = vec2(texCoord.x, 1.0 - texCoord.y);\n"
        "}";


static const char F_SHADER_OES[] =
        "#version 300 es\n"
        "#extension GL_OES_EGL_image_external_essl3 : require\n"
        "precision mediump float;\n"
        "in vec2 fTexCoord;\n"
        "uniform samplerExternalOES oesTexture;\n"
        "out vec4 fColor;\n"
        "void main()\n"
        "{\n"
        "    fColor = texture(oesTexture, fTexCoord);\n"
        "}";

static const char F_SHADER_2D[] =
        "#version 300 es\n"
        "precision mediump float;\n"

        "in vec2 fTexCoord;\n"
        "out vec4 color;\n"

        "uniform sampler2D layer;\n"

        "void main()\n"
        "{\n"
        "    color = texture(layer, fTexCoord);\n"
        "}\n";


#endif //ANDROIDBOILERPLATE_SHADER_H
