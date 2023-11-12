/**
 *  author : sz
 *  date : 2023/7/18
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_SHADER_H
#define ANDROIDBOILERPLATE_SHADER_H


static const char V_SHADER_BASIC[] =
        "#version 300 es\n"
        "layout (location = 0)\n"
        "in vec4 v_position;\n"
        "\n"
        "layout (location = 1)\n"
        "in vec4 v_color;\n"
        "\n"
        "out vec4 v_color2;\n"
        "void main()\n"
        "{\n"
        "    gl_Position = v_position;\n"
        "    v_color2 = v_color;\n"
        "}";

static const char F_SHADER_BASIC[] =
        "#version 300 es\n"
        "precision mediump float;\n"
        "\n"
        "in vec4 v_color2;\n"
        "\n"
        "out vec4 f_color;\n"
        "\n"
        "void main()\n"
        "{\n"
        "    f_color = v_color2;\n"
        "}";


#endif //ANDROIDBOILERPLATE_SHADER_H
